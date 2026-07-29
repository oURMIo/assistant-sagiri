package com.dach.sagiri.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.dach.sagiri.property.ClusterProperty;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

@Service
public class ClusterMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(ClusterMonitorService.class);

    private static final long CHECK_INTERVAL_MS = 3 * 60 * 1000L;
    private static final int ATTEMPTS_PER_PASS = 3;
    private static final int FAILED_PASS_THRESHOLD = 2;

    private final TelegramBot bot;
    private final WebService webService;
    private final FileService fileService;
    private final Map<String, String> clusterDomains = new LinkedHashMap<>();
    private final Map<String, Integer> failedPassCount = new ConcurrentHashMap<>();

    public ClusterMonitorService(TelegramBot bot,
        WebService webService,
        FileService fileService,
        ClusterProperty clusterProperty) {
        this.bot = bot;
        this.webService = webService;
        this.fileService = fileService;
        clusterDomains.put("Cluster 1", clusterProperty.getCluster1Domain());
        clusterDomains.put("Cluster 2", clusterProperty.getCluster2Domain());
    }

    @Scheduled(fixedDelay = CHECK_INTERVAL_MS)
    public void checkClusters() {
        clusterDomains.forEach(this::checkCluster);
    }

    private void checkCluster(String name, String domain) {
        if (isAvailable(domain)) {
            Integer failedPasses = failedPassCount.remove(name);
            if (failedPasses != null && failedPasses >= FAILED_PASS_THRESHOLD) {
                logger.warn("Cluster '{}' available", name);
                notifyAdmin("✅ " + name + " (" + domain + ") is back online");
            }
            return;
        }

        int failedPasses = failedPassCount.merge(name, 1, Integer::sum);
        logger.warn("Cluster '{}' failed check pass {} of {} in a row", name, failedPasses, FAILED_PASS_THRESHOLD);

        if (failedPasses == FAILED_PASS_THRESHOLD) {
            notifyAdmin("❌ " + name + " (" + domain + ") is unavailable!"
                        + " Failed check passes in a row: " + FAILED_PASS_THRESHOLD);
        }
    }

    private boolean isAvailable(String domain) {
        for (int attempt = 0; attempt < ATTEMPTS_PER_PASS; attempt++) {
            if (webService.checkClusterStatus(domain)) {
                return true;
            }
        }
        return false;
    }

    private void notifyAdmin(String text) {
        Optional<Long> adminChatId = fileService.getAdminChatId();
        if (adminChatId.isEmpty()) {
            logger.warn("Admin chatId is unknown, notification is skipped: '{}'", text);
            return;
        }

        try {
            long chatId = adminChatId.get();
            bot.execute(new SendMessage(chatId, text));
        } catch (Exception e) {
            logger.error("Failed to notify admin with text:'{}'", text, e);
        }
    }
}
