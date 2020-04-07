package org.abigballofmud.flink.platform.app.service.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.codingdebugallday.client.api.dto.ClusterDTO;
import com.github.codingdebugallday.client.infra.context.FlinkApiContext;
import com.github.codingdebugallday.client.infra.utils.FlinkCommonUtil;
import com.github.codingdebugallday.client.infra.utils.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.platform.api.dto.UdfDTO;
import org.abigballofmud.flink.platform.app.service.UdfService;
import org.abigballofmud.flink.platform.domain.entity.Udf;
import org.abigballofmud.flink.platform.infra.constants.CommonConstant;
import org.abigballofmud.flink.platform.infra.converter.UdfConvertMapper;
import org.abigballofmud.flink.platform.infra.enums.UdfTypeEnum;
import org.abigballofmud.flink.platform.infra.execeptions.ClassLoaderException;
import org.abigballofmud.flink.platform.infra.loader.ExtClasspathLoader;
import org.abigballofmud.flink.platform.infra.loader.GroovyCompiler;
import org.abigballofmud.flink.platform.infra.mapper.UdfMapper;
import org.abigballofmud.flink.platform.infra.utils.CommonUtil;
import org.abigballofmud.flink.platform.infra.utils.FlinkUtil;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/01 11:46
 * @since 1.0
 */
@Service
@Slf4j
public class UdfServiceImpl extends ServiceImpl<UdfMapper, Udf> implements UdfService {

    private final UdfMapper udfMapper;
    private final ExecutorService executorService = ThreadPoolUtil.getExecutorService();

    @Resource
    private FlinkApiContext flinkApiContext;
    @Resource
    private StringEncryptor jasyptStringEncryptor;

    public UdfServiceImpl(UdfMapper udfMapper) {
        this.udfMapper = udfMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UdfDTO insert(UdfDTO udfDTO, MultipartFile multipartFile) {
        // 插表
        Udf udf = UdfConvertMapper.INSTANCE.dtoToEntity(udfDTO);
        udf.setUdfStatus(CommonConstant.Status.UPLOADING);
        udfMapper.insert(udf);
        ClusterDTO clusterDTO = flinkApiContext.get(udfDTO.getClusterCode(), udfDTO.getTenantId())
                .getApiClient().getClusterDTO();
        // 判断udfType
        Class<?> clazz;
        if (udfDTO.getUdfType().equalsIgnoreCase(UdfTypeEnum.CODE.name())) {
            // 动态编译源码
            log.info("add udf[{}] by java code", udfDTO.getUdfName());
            clazz = GroovyCompiler.compile(udfDTO.getContent(), udfDTO.getUdfName());
        } else if (udfDTO.getUdfType().equalsIgnoreCase(UdfTypeEnum.JAR.name())) {
            log.info("add udf[{}] by jar", udfDTO.getUdfName());
            // 加载jar包中的udf类 并上传到flink集群
            File file = FlinkCommonUtil.multiPartFileToFile(multipartFile);
            clazz = loadUdfJar(udfDTO, file);
            // 异步将udf jar包上传到集群
            uploadUdfJar(file, udf, clusterDTO);
        } else {
            throw new UnsupportedOperationException("invalid udfType, just supported [jar,code]");
        }
        Object udfObject;
        try {
            udfObject = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ClassLoaderException("class newInstance error", e);
        }
        // 注册udf
        FlinkUtil flinkUtil = new FlinkUtil();
        flinkUtil.registerFunction(udfDTO.getUdfName(), udfObject);
        return UdfConvertMapper.INSTANCE.entityToDTO(udf);
    }

    private void uploadUdfJar(File file, Udf udf, ClusterDTO clusterDTO) {
        CompletableFuture<Void> allCompletableFuture = CommonUtil.uploadFileToFlinkCluster(
                clusterDTO.getNodeDTOList(), file,
                String.format("%d_%s", udf.getTenantId(), file.getName()),
                udf.getUdfJarPath(), jasyptStringEncryptor, executorService);
        allCompletableFuture.thenRunAsync(() -> {
            udf.setUdfStatus(CommonConstant.Status.UPLOADED);
            udfMapper.updateById(udf);
        }, executorService);
    }

    private Class<?> loadUdfJar(UdfDTO udfDTO, File file) {
        try {
            ExtClasspathLoader.loadClasspath(file);
            return Class.forName(udfDTO.getContent());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException | ClassNotFoundException e) {
            throw new ClassLoaderException("classloader error", e);
        }
    }


}
