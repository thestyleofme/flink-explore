package org.abigballofmud.flink.platform.infra.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.github.codingdebugallday.client.api.dto.NodeDTO;
import com.github.codingdebugallday.client.api.dto.NodeSettingInfo;
import com.github.codingdebugallday.client.infra.handlers.FutureTaskWorker;
import com.github.codingdebugallday.client.infra.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jasypt.encryption.StringEncryptor;

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
