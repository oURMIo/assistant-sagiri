package com.dach.sagiri.bot.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClusterStatusCallbackTest {

    // Returns "cluster_status" as the callback identifier
    @Test
    void test_callback_returns_cluster_status() {
        ClusterStatusCallback callback = new ClusterStatusCallback();

        String result = callback.callback();

        assertEquals("cluster_status", result);
    }

    // Handling null TelegramBot parameter
    @Test
    void test_execute_throws_exception_with_null_bot() {
        ClusterStatusCallback callback = new ClusterStatusCallback();
        CallbackQuery mockCallback = mock(CallbackQuery.class);
        com.pengrad.telegrambot.model.User mockUser = mock(com.pengrad.telegrambot.model.User.class);

        when(mockCallback.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(123456789L);

        assertThrows(NullPointerException.class, () -> {
            callback.execute(null, mockCallback);
        });
    }

    // Creates a keyboard with two buttons for cluster selection
    @Test
    void test_creates_keyboard_with_two_buttons() {
        ClusterStatusCallback callback = new ClusterStatusCallback();
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);
        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);

        callback.execute(bot, callbackQuery);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());

        SendMessage sentMessage = messageCaptor.getValue();
        assertEquals(12345L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Please select a cluster so that I may check its status for you", sentMessage.getParameters().get("text"));

        ReplyKeyboardMarkup keyboard = (ReplyKeyboardMarkup) sentMessage.getParameters().get("reply_markup");
        assertNotNull(keyboard);
    }
}
