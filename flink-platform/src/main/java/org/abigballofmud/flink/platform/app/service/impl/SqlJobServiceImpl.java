package org.abigballofmud.flink.platform.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.codingdebugallday.client.api.dto.NodeDTO;
import com.github.codingdebugallday.client.domain.repository.NodeRepository;
import com.github.codingdebugallday.client.infra.exceptions.FlinkCommonException;
import com.github.codingdebugallday.client.infra.utils.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.platform.api.dto.SqlJobDTO;
import org.abigballofmud.flink.platform.app.service.SqlJobService;
import org.abigballofmud.flink.platform.domain.entity.SqlJob;
import org.abigballofmud.flink.platform.infra.constants.CommonConstant;
import org.abigballofmud.flink.platform.infra.converter.SqlJobConvertMapper;
import org.abigballofmud.flink.platform.infra.mapper.SqlJobMapper;
import org.abigballofmud.flink.platform.infra.utils.CommonUtil;
import org.apache.commons.io.FileUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
public class SqlJobServiceImpl extends ServiceImpl<SqlJobMapper, SqlJob> implements SqlJobService {

    private final ExecutorService executorService = ThreadPoolUtil.getExecutorService();

    @Resource
    private StringEncryptor jasyptStringEncryptor;

    private final NodeRepository nodeRepository;
    private final SqlJobMapper sqlJobMapper;

    public SqlJobServiceImpl(NodeRepository nodeRepository,
                             SqlJobMapper sqlJobMapper) {
        this.nodeRepository = nodeRepository;
        this.sqlJobMapper = sqlJobMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SqlJobDTO create(SqlJobDTO sqlJobDTO) {
        // 插表 状态created
        SqlJob sqlJob = SqlJobConvertMapper.INSTANCE.dtoToEntity(sqlJobDTO);
        sqlJob.setJobStatus(CommonConstant.Status.UPLOADING);
        sqlJobMapper.insert(sqlJob);
        // 异步上传sql脚本
        String sqlFileName = String.format("%d_%s.sql", sqlJobDTO.getTenantId(), sqlJobDTO.getJobCode());
        uploadSqlFile(genSqlFile(sqlFileName, sqlJobDTO), sqlFileName, sqlJob);
        return SqlJobConvertMapper.INSTANCE.entityToDTO(sqlJob);
    }

    private File genSqlFile(String sqlFileName, SqlJobDTO sqlJobDTO) {
        File sqlFile = new File(sqlFileName);
        try {
            FileUtils.writeStringToFile(sqlFile, sqlJobDTO.getContent(), StandardCharsets.UTF_8);
            return sqlFile;
        } catch (IOException e) {
            log.error("write sql content to file error");
            throw new FlinkCommonException("write sql content to file error", e);
        }
    }

    private void uploadSqlFile(File sqlFile, String sqlFileName, SqlJob sqlJob) {
        // sql文件异步上传到flink cluster
        List<NodeDTO> nodeDTOList =
                nodeRepository.selectByClusterCode(sqlJob.getClusterCode(), sqlJob.getTenantId());
        if (CollectionUtils.isEmpty(nodeDTOList)) {
            throw new FlinkCommonException("error.find.flink.cluster");
        }
        CompletableFuture<Void> allCompletableFuture = CommonUtil.uploadFileToFlinkCluster(
                nodeDTOList, sqlFile, sqlFileName,
                sqlJob.getSqlUploadPath(), jasyptStringEncryptor, executorService);
        // 上传完成后更改状态
        allCompletableFuture.thenRunAsync(() -> {
            sqlJob.setJobStatus(CommonConstant.Status.UPLOADED);
            sqlJobMapper.updateById(sqlJob);
        }, executorService);
    }

}
