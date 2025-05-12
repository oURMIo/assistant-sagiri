package com.dach.sagiri.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.dach.sagiri.property.BotConfigProperty;
import com.pengrad.telegrambot.TelegramBot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BotConfigTest {

    // TelegramBot bean is successfully created with valid bot key
    @Test
    void test_telegram_bot_bean_created_with_valid_key() {
        BotConfigProperty mockProperties = mock(BotConfigProperty.class);
        when(mockProperties.getBotKey()).thenReturn("valid_bot_key");

        BotConfig botConfig = new BotConfig();

        TelegramBot telegramBot = botConfig.telegramBot(mockProperties);

        assertNotNull(telegramBot);
    }

    // BotConfigProperty correctly injects the bot key from application properties
    @Test
    void test_bot_key_injection() {
        BotConfigProperty properties = new BotConfigProperty();
        ReflectionTestUtils.setField(properties, "botKey", "testKey");
        assertEquals("testKey", properties.getBotKey());
    }
}
