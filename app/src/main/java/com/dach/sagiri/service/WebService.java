package com.dach.sagiri.service;

import java.io.IOException;
import java.net.InetAddress;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebService.class);
    private static final int CHECK_STATUS_TIMEOUT_MS = 4000;

    public boolean checkClusterStatus(@NotNull String clusterDomain) {
        try {
            InetAddress address = InetAddress.getByName(clusterDomain);
            boolean isAvailable = address.isReachable(CHECK_STATUS_TIMEOUT_MS);
            if (!isAvailable) {
                logger.warn("Cluster '{}' have status unavailable after checking", clusterDomain);
            }
            return isAvailable;
        } catch (IOException e) {
            logger.error("Got exception while check status cluster:'{}'", clusterDomain);
            return false;
        }
    }
}
