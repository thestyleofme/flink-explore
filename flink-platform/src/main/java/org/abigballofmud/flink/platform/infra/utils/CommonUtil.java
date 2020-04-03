package org.abigballofmud.flink.platform.infra.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.github.codingdebugallday.client.api.dto.NodeDTO;
import com.github.codingdebugallday.client.api.dto.NodeSettingInfo;
import com.github.codingdebugallday.client.infra.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.platform.infra.execeptions.ClassLoaderException;
import org.abigballofmud.flink.platform.infra.handlers.FutureTaskWorker;
import org.apache.commons.io.FileUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/02 10:55
 * @since 1.0
 */
@Slf4j
public class CommonUtil {

    private CommonUtil() {
        throw new IllegalStateException("util class");
    }

    public static File multiPartFileToFile(MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "multipartFile must not be null");
        try {
            File toFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            byte[] bytes = FileCopyUtils.copyToByteArray(multipartFile.getInputStream());
            FileCopyUtils.copy(bytes, toFile);
            return toFile;
        } catch (IOException e) {
            throw new ClassLoaderException("multiPartFileToFile error", e);
        }
    }

    /**
     * 将file上传到flink cluster
     *
     * @param nodeDTOList           flink node list
     * @param file                  wait to upload file
     * @param remoteFileName        在服务器上文件的名称
     * @param uploadPath            上传到服务器的路径
     * @param jasyptStringEncryptor StringEncryptor
     * @param executorService       ExecutorService
     * @return java.util.concurrent.CompletableFuture<java.lang.Void>
     */
    public static CompletableFuture<Void> uploadFileToFlinkCluster(List<NodeDTO> nodeDTOList,
                                                            File file,
                                                            String remoteFileName,
                                                            String uploadPath,
                                                            StringEncryptor jasyptStringEncryptor,
                                                            ExecutorService executorService) {
        FutureTaskWorker<NodeDTO, Boolean> futureTaskWorker = new FutureTaskWorker<>(nodeDTOList,
                nodeDTO -> CompletableFuture.supplyAsync(() -> {
                    NodeSettingInfo nodeSettingInfo = JSON.toObj(nodeDTO.getSettingInfo(), NodeSettingInfo.class);
                    try (Ssh2Util ssh2Util = new Ssh2Util(nodeSettingInfo.getHost(), nodeSettingInfo.getUsername(),
                            jasyptStringEncryptor.decrypt(nodeSettingInfo.getPassword()))) {
                        ssh2Util.upload(FileUtils.readFileToByteArray(file), remoteFileName, uploadPath);
                    } catch (IOException e) {
                        log.error("file upload to cluster error", e);
                        return false;
                    }
                    return true;
                }, executorService)
        );
        return futureTaskWorker.getAllCompletableFuture();
    }
}
