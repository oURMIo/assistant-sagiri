package com.dach.sagiri.bot.callback;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.dach.sagiri.service.UrlService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsefulUrlCallbackTest {

    // Callback returns the string "useful_urls"
    @Test
    void test_callback_returns_useful_urls() {
        UrlService urlService = mock(UrlService.class);
        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(urlService);

        String callbackValue = usefulUrlCallback.callback();

        assertEquals("useful_urls", callbackValue);
    }

    // Handle case when urlService.getAllUrls() returns empty map
    @Test
    void test_execute_with_empty_url_map() {
        UrlService urlService = mock(UrlService.class);
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(123456789L);
        when(urlService.getAllUrls()).thenReturn(Map.of());

        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(urlService);
        usefulUrlCallback.execute(bot, callbackQuery);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());

        SendMessage capturedMessage = messageCaptor.getValue();
        assertEquals(123456789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals("Useful URLs:", capturedMessage.getParameters().get("text"));

        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) capturedMessage.getParameters().get("reply_markup");
        assertNotNull(markup);
    }

    // Execute method creates an InlineKeyboardMarkup with Domain Manager and Family Drive buttons
    @Test
    void test_execute_creates_inline_keyboard_markup_with_buttons() {
        UrlService urlService = mock(UrlService.class);
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callback = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(callback.from()).thenReturn(user);
        when(user.id()).thenReturn(123L);

        Map<UrlService.UrlType, String> urlMap = Map.of(
            UrlService.UrlType.DOMAIN_MANAGER, "http://domain-manager.com",
            UrlService.UrlType.GOOGLE_DRIVE_FAMILY, "http://family-drive.com"
        );
        when(urlService.getAllUrls()).thenReturn(urlMap);

        UsefulUrlCallback usefulUrlCallback = new UsefulUrlCallback(urlService);

        usefulUrlCallback.execute(bot, callback);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());
        SendMessage capturedMessage = messageCaptor.getValue();

        assertEquals(123L, capturedMessage.getParameters().get("chat_id"));
        assertEquals("Useful URLs:", capturedMessage.getParameters().get("text"));

        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) capturedMessage.getParameters().get("reply_markup");
        InlineKeyboardButton[][] keyboard = markup.inlineKeyboard();

        assertEquals(1, keyboard.length);
        assertEquals(2, keyboard[0].length);

        assertEquals("Domain Manager", keyboard[0][0].text());
        assertEquals("http://domain-manager.com", keyboard[0][0].url());
        assertEquals("Family Drive", keyboard[0][1].text());
        assertEquals("http://family-drive.com", keyboard[0][1].url());
    }
}
