package com.dach.sagiri.bot.command;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartCommandTest {

    // command() method returns "/start" string
    @Test
    void test_command_returns_start_string() {
        StartCommand startCommand = new StartCommand();

        String result = startCommand.command();

        assertEquals("/start", result);
    }

    // execute() handles null message parameter
    @Test
    void test_execute_handles_null_message() {
        StartCommand startCommand = new StartCommand();
        TelegramBot mockBot = mock(TelegramBot.class);

        assertThrows(NullPointerException.class, () -> startCommand.execute(mockBot, null));
    }

    // Correctly formats greeting message with user's first name
    @Test
    void test_formats_greeting_with_first_name() {
        TelegramBot mockBot = mock(TelegramBot.class);
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);
        Chat mockChat = mock(Chat.class);

        when(mockMessage.from()).thenReturn(mockUser);
        when(mockUser.firstName()).thenReturn("John");
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(123456L);

        StartCommand startCommand = new StartCommand();

        startCommand.execute(mockBot, mockMessage);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(mockBot).execute(sendMessageCaptor.capture());

        SendMessage capturedSendMessage = sendMessageCaptor.getValue();
        assertEquals(123456L, capturedSendMessage.getParameters().get("chat_id"));
        assertTrue(capturedSendMessage.getParameters().get("text").toString().startsWith("Greetings, John\n"));
    }

    @Test
    void test_handles_null_or_empty_first_name() {
        TelegramBot mockBot = mock(TelegramBot.class);
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);
        Chat mockChat = mock(Chat.class);

        when(mockMessage.from()).thenReturn(mockUser);
        when(mockUser.firstName()).thenReturn(null);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(123456L);

        StartCommand startCommand = new StartCommand();

        startCommand.execute(mockBot, mockMessage);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(mockBot).execute(sendMessageCaptor.capture());

        SendMessage capturedSendMessage = sendMessageCaptor.getValue();
        assertEquals(123456L, capturedSendMessage.getParameters().get("chat_id"));
        assertTrue(capturedSendMessage.getParameters().get("text").toString().startsWith("Greetings, null\n"));
    }
}
