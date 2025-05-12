package com.dach.sagiri.bot.command;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.dach.sagiri.property.ClusterProperty;
import com.dach.sagiri.service.WebService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatusClusterAllCommandTest {

    // Command returns "/status_cluster_all" string when command() is called
    @Test
    void test_command_returns_correct_string() {
        WebService webService = mock(WebService.class);
        ClusterProperty clusterProperty = mock(ClusterProperty.class);
        StatusClusterAllCommand command = new StatusClusterAllCommand(webService, clusterProperty);

        String result = command.command();

        assertEquals("/status_cluster_all", result);
    }

    // Both clusters are unavailable
    @Test
    void test_both_clusters_unavailable() {
        WebService webService = mock(WebService.class);
        ClusterProperty clusterProperty = mock(ClusterProperty.class);

        when(clusterProperty.getCluster1Domain()).thenReturn("cluster1.example.com");
        when(clusterProperty.getCluster2Domain()).thenReturn("cluster2.example.com");
        when(webService.checkClusterStatus("cluster1.example.com")).thenReturn(false);
        when(webService.checkClusterStatus("cluster2.example.com")).thenReturn(false);

        TelegramBot bot = mock(TelegramBot.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        StatusClusterAllCommand command = new StatusClusterAllCommand(webService, clusterProperty);

        command.execute(bot, message);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(2)).execute(messageCaptor.capture());

        List<SendMessage> capturedMessages = messageCaptor.getAllValues();
        String resultMessage = capturedMessages.get(1).getParameters().get("text").toString();

        assertTrue(resultMessage.contains("Cluster 1: ❌ Unavailable"));
        assertTrue(resultMessage.contains("Cluster 2: ❌ Unavailable"));
    }

    // Execute method sends final status message with cluster availability
    @Test
    void test_execute_sends_final_status_message() {
        // Arrange
        WebService webService = mock(WebService.class);
        ClusterProperty clusterProperty = mock(ClusterProperty.class);

        String cluster1Domain = "cluster1.com";
        String cluster2Domain = "cluster2.com";
        when(clusterProperty.getCluster1Domain()).thenReturn(cluster1Domain);
        when(clusterProperty.getCluster2Domain()).thenReturn(cluster2Domain);

        when(webService.checkClusterStatus(cluster1Domain)).thenReturn(true);
        when(webService.checkClusterStatus(cluster2Domain)).thenReturn(false);

        StatusClusterAllCommand command = new StatusClusterAllCommand(webService, clusterProperty);

        TelegramBot bot = mock(TelegramBot.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        long chatId = 123L;

        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);

        // Act
        command.execute(bot, message);

        // Assert
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(2)).execute(messageCaptor.capture());

        List<SendMessage> capturedMessages = messageCaptor.getAllValues();
        assertEquals(2, capturedMessages.size());

        SendMessage initialMessage = capturedMessages.getFirst();
        assertEquals(chatId, initialMessage.getParameters().get("chat_id"));
        assertEquals("I shall now scurry off to inspect the status of the clusters...", initialMessage.getParameters().get("text"));

        SendMessage statusMessage = capturedMessages.get(1);
        assertEquals(chatId, statusMessage.getParameters().get("chat_id"));
        assertEquals("Status:\nCluster 1: ✅ Available\nCluster 2: ❌ Unavailable", statusMessage.getParameters().get("text"));
    }

    // CheckServers correctly formats message with available status when both clusters are up
    @Test
    void test_check_servers_both_clusters_up() {
        WebService webService = mock(WebService.class);
        ClusterProperty clusterProperty = mock(ClusterProperty.class);
        when(clusterProperty.getCluster1Domain()).thenReturn("cluster1.com");
        when(clusterProperty.getCluster2Domain()).thenReturn("cluster2.com");

        when(webService.checkClusterStatus("cluster1.com")).thenReturn(true);
        when(webService.checkClusterStatus("cluster2.com")).thenReturn(true);

        StatusClusterAllCommand command = new StatusClusterAllCommand(webService, clusterProperty);

        String result = command.checkServers();

        assertEquals("Status:\nCluster 1: ✅ Available\nCluster 2: ✅ Available", result);
    }
}
