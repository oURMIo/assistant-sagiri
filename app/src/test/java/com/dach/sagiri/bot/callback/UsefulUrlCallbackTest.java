package com.dach.sagiri.bot.callback;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.dach.sagiri.service.FileService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsefulUrlCallbackTest {

    // Returns "useful_urls" as the callback identifier
    @Test
    void test_callback_returns_useful_urls() {
        FileService fileService = mock(FileService.class);
        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(fileService);

        String callbackIdentifier = usefulUrlCallback.callback();

        assertEquals("useful_urls", callbackIdentifier);
    }

    // Handles empty list of URLs by sending an error message
    @Test
    void test_execute_handles_empty_url_list() {
        FileService fileService = mock(FileService.class);
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(123456789L);
        when(fileService.getUsefulUrls()).thenReturn(Optional.empty());

        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(fileService);

        usefulUrlCallback.execute(bot, callbackQuery);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());

        SendMessage capturedMessage = messageCaptor.getValue();
        assertEquals(123456789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals("Can't find useful urls இ௰இ", capturedMessage.getParameters().get("text"));
    }
}
