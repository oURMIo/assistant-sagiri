package com.dach.sagiri.bot.callback;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.dach.sagiri.property.dto.UsefulUrl;
import com.dach.sagiri.service.UrlService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsefulUrlCallbackTest {

    // Returns "useful_urls" as the callback identifier
    @Test
    void test_callback_returns_useful_urls() {
        UrlService urlService = mock(UrlService.class);

        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(urlService);

        assertEquals("useful_urls", usefulUrlCallback.callback());
    }

    // Handles empty list of URLs by sending an error message
    @Test
    void test_execute_handles_empty_url_list() {
        UrlService urlService = mock(UrlService.class);
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(123456789L);
        when(urlService.getUsefulUrls()).thenReturn(Optional.empty());

        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(urlService);
        usefulUrlCallback.execute(bot, callbackQuery);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());

        SendMessage capturedMessage = messageCaptor.getValue();
        assertEquals(123456789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals("Haven't any url இ௰இ", capturedMessage.getParameters().get("text"));
    }

    // Creates inline keyboard buttons for each URL with proper name and URL
    @Test
    void test_creates_inline_keyboard_buttons() {
        // Arrange
        UrlService urlService = mock(UrlService.class);
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);
        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(urlService);

        List<UsefulUrl> urls = List.of(
            new UsefulUrl("Google", "https://google.com", "Search Engine"),
            new UsefulUrl("GitHub", "https://github.com", "Code Hosting")
        );

        when(urlService.getUsefulUrls()).thenReturn(Optional.of(urls));
        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);

        // Act
        usefulUrlCallback.execute(bot, callbackQuery);

        // Assert
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());

        SendMessage sentMessage = messageCaptor.getValue();
        assertEquals(12345L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Useful URLs:", sentMessage.getParameters().get("text"));

        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) sentMessage.getParameters().get("reply_markup");
        InlineKeyboardButton[][] keyboard = markup.inlineKeyboard();

        assertEquals(2, keyboard.length);
        assertEquals("Google", keyboard[0][0].text());
        assertEquals("https://google.com", keyboard[0][0].url());
        assertEquals("GitHub", keyboard[1][0].text());
        assertEquals("https://github.com", keyboard[1][0].url());
    }
}
