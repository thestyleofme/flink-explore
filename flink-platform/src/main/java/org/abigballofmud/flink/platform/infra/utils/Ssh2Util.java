package org.abigballofmud.flink.platform.infra.utils;

import java.io.IOException;
import java.util.Optional;

import com.github.codingdebugallday.client.infra.exceptions.FlinkCommonException;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/02 10:32
 * @since 1.0
 */
@Slf4j
public class Ssh2Util implements AutoCloseable {

    private final Connection connection;

    public Ssh2Util(String hostname, String username, String password) {
        this.connection = initConnection(hostname, username, password);
    }

    private Connection initConnection(String hostname, String username, String password) {
        try {
            Connection conn = new Connection(hostname);
            // 连接
            conn.connect(null, 60000, 30000);
            boolean isSuccess = conn.authenticateWithPassword(username, password);
            if (isSuccess) {
                log.info("Successfully connected to {}", hostname);
            }
            return conn;
        } catch (IOException e) {
            log.error("Connect to server failed");
            throw new FlinkCommonException("Connect to server failed", e);
        }
    }

    public void upload(byte[] data, String remoteFileName, String remoteTargetDirectory) {
        try {
            SCPClient scpClient = connection.createSCPClient();
            scpClient.put(data, remoteFileName, remoteTargetDirectory);
            log.info("Successfully upload file: {}", remoteFileName);
        } catch (IOException e) {
            log.error("Upload file to server failed");
            throw new FlinkCommonException("Upload file to server failed", e);
        }
    }


    @Override
    public void close() throws IOException {
        Optional.ofNullable(connection).ifPresent(Connection::close);
    }
}
