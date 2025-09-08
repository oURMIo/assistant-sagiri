package com.dach.sagiri.service;

import java.net.InetSocketAddress;
import java.net.Socket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebService.class);

    private static final int CHECK_STATUS_TIMEOUT_MS = 4000;
    private static final int SSH_PORT = 22;

    public boolean checkClusterStatus(@NotNull String clusterDomain) {
        boolean isAvailable = testConnection(clusterDomain);
        if (!isAvailable) {
            logger.warn("Cluster '{}' have status unavailable after checking", clusterDomain);
        }

        return isAvailable;
    }

    private boolean testConnection(String host) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, SSH_PORT), CHECK_STATUS_TIMEOUT_MS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
