package com.dach.sagiri.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.dach.sagiri.property.ClusterProperty;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClusterMonitorServiceTest {

    private static final String CLUSTER_1_DOMAIN = "cluster-one.net";
    private static final String CLUSTER_2_DOMAIN = "cluster-two.net";
    private static final long ADMIN_CHAT_ID = 100L;
    private static final String CLUSTER_1_DOWN =
        "❌ Cluster 1 (" + CLUSTER_1_DOMAIN + ") is unavailable! Failed check passes in a row: 2";
    private static final String CLUSTER_1_UP =
        "✅ Cluster 1 (" + CLUSTER_1_DOMAIN + ") is back online";

    private final TelegramBot bot = mock(TelegramBot.class);
    private final WebService webService = mock(WebService.class);
    private final FileService fileService = mock(FileService.class);

    // Both clusters available - nothing is reported
    @Test
    void test_no_notification_when_all_clusters_are_available() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(true);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(3);

        verify(bot, never()).execute(any());
    }

    // An available cluster is not probed again within the same pass
    @Test
    void test_pass_stops_after_first_successful_attempt() {
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(true);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(1);

        verify(webService).checkClusterStatus(CLUSTER_1_DOMAIN);
        verify(webService).checkClusterStatus(CLUSTER_2_DOMAIN);
    }

    // A failed pass retries the cluster three times before giving up
    @Test
    void test_failed_pass_makes_three_attempts() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(1);

        verify(webService, times(3)).checkClusterStatus(CLUSTER_1_DOMAIN);
    }

    // A cluster answering on the last attempt of a pass counts as available
    @Test
    void test_pass_is_successful_when_last_attempt_answers() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false, false, true);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(1);

        verify(bot, never()).execute(any());
    }

    // A single failed pass is not reported yet
    @Test
    void test_no_notification_after_single_failed_pass() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(1);

        verify(bot, never()).execute(any());
    }

    // The second failed pass in a row is reported to the admin chat
    @Test
    void test_notifies_admin_after_two_failed_passes() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(2);

        SendMessage sent = captureSingleMessage();
        assertEquals(ADMIN_CHAT_ID, sent.getParameters().get("chat_id"));
        assertEquals(CLUSTER_1_DOWN, sent.getParameters().get("text"));
    }

    // Each unavailable cluster produces its own message
    @Test
    void test_notifies_about_every_unavailable_cluster() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(false);

        runPasses(2);

        List<String> texts = captureTexts(2);
        assertTrue(texts.get(0).startsWith("❌ Cluster 1"));
        assertTrue(texts.get(1).startsWith("❌ Cluster 2"));
    }

    // A cluster that stays down is reported only once, not on every pass
    @Test
    void test_does_not_repeat_notification_while_cluster_stays_down() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(10);

        captureSingleMessage();
    }

    // Recovery of an already reported cluster is reported as well
    @Test
    void test_notifies_admin_when_reported_cluster_is_back_online() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        // two failed passes (three attempts each), then an available cluster
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN))
            .thenReturn(false, false, false, false, false, false, true);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(3);

        List<String> texts = captureTexts(2);
        assertEquals(CLUSTER_1_DOWN, texts.get(0));
        assertEquals(CLUSTER_1_UP, texts.get(1));
    }

    // The whole outage produces exactly two messages: one down and one back online
    @Test
    void test_outage_produces_exactly_one_down_and_one_recovery_message() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        // two failed passes (three attempts each), then the cluster stays available
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN))
            .thenReturn(false, false, false, false, false, false, true);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(10);

        List<String> texts = captureTexts(2);
        assertEquals(CLUSTER_1_DOWN, texts.get(0));
        assertEquals(CLUSTER_1_UP, texts.get(1));
    }

    // Recovery is silent when only one pass failed and nothing was reported
    @Test
    void test_no_recovery_notification_when_down_was_never_reported() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        // one failed pass (three attempts), then an available cluster
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false, false, false, true);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(2);

        verify(bot, never()).execute(any());
    }

    // A successful pass resets the counter, so separate glitches never add up
    @Test
    void test_successful_pass_resets_the_failed_pass_counter() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        // failed pass, successful pass, failed pass again
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN))
            .thenReturn(false, false, false, true, false, false, false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(3);

        verify(bot, never()).execute(any());
    }

    // Without a known admin chat id nothing is sent and the pass does not fail
    @Test
    void test_skips_notification_when_admin_chat_id_is_unknown() {
        when(fileService.getAdminChatId()).thenReturn(Optional.empty());
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(true);

        runPasses(2);

        verify(bot, never()).execute(any());
    }

    // Telegram failure is swallowed so the scheduled pass keeps running
    @Test
    void test_telegram_failure_does_not_break_the_pass() {
        when(fileService.getAdminChatId()).thenReturn(Optional.of(ADMIN_CHAT_ID));
        when(webService.checkClusterStatus(CLUSTER_1_DOMAIN)).thenReturn(false);
        when(webService.checkClusterStatus(CLUSTER_2_DOMAIN)).thenReturn(false);
        when(bot.execute(any())).thenThrow(new RuntimeException("telegram is down"));

        runPasses(2);

        captureTexts(2);
    }

    private void runPasses(int passes) {
        ClusterMonitorService monitor = newService();
        for (int i = 0; i < passes; i++) {
            monitor.checkClusters();
        }
    }

    private ClusterMonitorService newService() {
        ClusterProperty clusterProperty = mock(ClusterProperty.class);
        when(clusterProperty.getCluster1Domain()).thenReturn(CLUSTER_1_DOMAIN);
        when(clusterProperty.getCluster2Domain()).thenReturn(CLUSTER_2_DOMAIN);
        return new ClusterMonitorService(bot, webService, fileService, clusterProperty);
    }

    private SendMessage captureSingleMessage() {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        return captor.getValue();
    }

    private List<String> captureTexts(int expectedCount) {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(expectedCount)).execute(captor.capture());
        return captor.getAllValues().stream()
            .map(message -> String.valueOf(message.getParameters().get("text")))
            .toList();
    }
}
