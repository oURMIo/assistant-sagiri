package com.dach.sagiri.bot.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PingPongCallbackTest {

    // Callback method returns "ping" string
    @Test
    void test_callback_returns_ping() {
        PingPongCallback pingPongCallback = new PingPongCallback();

        String callbackValue = pingPongCallback.callback();

        assertEquals("ping", callbackValue);
    }

    // Handle null TelegramBot instance
    @Test
    void test_execute_throws_exception_with_null_bot() {
        PingPongCallback pingPongCallback = new PingPongCallback();
        CallbackQuery mockCallback = mock(CallbackQuery.class);
        User mockUser = mock(User.class);

        when(mockCallback.id()).thenReturn("123456");
        when(mockCallback.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(987654L);

        assertThrows(NullPointerException.class, () -> pingPongCallback.execute(null, mockCallback));
    }

    // Executes AnswerCallbackQuery with callback.id() and "Pong 🏓" text
    @Test
    void test_answer_callback_query_execution() {
        // Arrange
        TelegramBot mockBot = mock(TelegramBot.class);
        CallbackQuery mockCallback = mock(CallbackQuery.class);
        User mockUser = mock(User.class);

        when(mockCallback.id()).thenReturn("callback123");
        when(mockCallback.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(12345L);

        PingPongCallback pingPongCallback = new PingPongCallback();

        pingPongCallback.execute(mockBot, mockCallback);

        ArgumentCaptor<AnswerCallbackQuery> callbackCaptor = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(mockBot).execute(callbackCaptor.capture());

        AnswerCallbackQuery capturedCallback = callbackCaptor.getValue();
        assertEquals("callback123", capturedCallback.getParameters().get("callback_query_id"));
        assertEquals("Pong 🏓", capturedCallback.getParameters().get("text"));
    }

    // Handles null bot parameter
    @Test
    void test_null_bot_parameter() {
        TelegramBot nullBot = null;
        CallbackQuery mockCallback = mock(CallbackQuery.class);
        PingPongCallback pingPongCallback = new PingPongCallback();

        assertThrows(NullPointerException.class, () -> pingPongCallback.execute(nullBot, mockCallback));
    }
}
