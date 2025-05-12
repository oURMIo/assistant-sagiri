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

class StatusCluster2CommandTest {

    // Command returns "/status_cluster2" when command() is called
    @Test
    void test_command_returns_correct_command_string() {
        WebService webService = mock(WebService.class);
        ClusterProperty clusterProperty = mock(ClusterProperty.class);

        StatusCluster2Command command = new StatusCluster2Command(webService, clusterProperty);

        assertEquals("/status_cluster2", command.command());
    }

    // CheckServers returns "❌ Unavailable" message when cluster is unavailable
    @Test
    void test_check_servers_returns_unavailable_message_when_cluster_is_down() {
        WebService webService = mock(WebService.class);
        ClusterProperty clusterProperty = mock(ClusterProperty.class);

        String testDomain = "test.cluster1.domain";
        when(clusterProperty.getCluster1Domain()).thenReturn(testDomain);
        when(webService.checkClusterStatus(testDomain)).thenReturn(false);

        StatusCluster2Command command = new StatusCluster2Command(webService, clusterProperty);

        TelegramBot bot = mock(TelegramBot.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        command.execute(bot, message);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(2)).execute(argumentCaptor.capture());

        List<SendMessage> capturedArguments = argumentCaptor.getAllValues();
        String resultMessage = capturedArguments.get(1).getParameters().get("text").toString();

        assertTrue(resultMessage.contains("❌ Unavailable"));
    }
}
