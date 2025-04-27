package com.dach.sagiri.bot;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import com.dach.sagiri.service.BotMessageService;
import com.pengrad.telegrambot.TelegramBot;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class BotInitializerTest {

    // Constructor properly initializes bot and botMessageService fields
    @Test
    void test_constructor_properly_initializes_fields() {
        TelegramBot mockBot = mock(TelegramBot.class);
        BotMessageService mockService = mock(BotMessageService.class);

        BotInitializer initializer = new BotInitializer(mockBot, mockService);

        try {
            Field botField = BotInitializer.class.getDeclaredField("bot");
            botField.setAccessible(true);
            Field serviceField = BotInitializer.class.getDeclaredField("botMessageService");
            serviceField.setAccessible(true);

            assertEquals(mockBot, botField.get(initializer), "Bot field should be initialized with the provided bot");
            assertEquals(mockService, serviceField.get(initializer),
                "BotMessageService field should be initialized with the provided service");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Exception occurred during reflection: " + e.getMessage());
        }
    }
}
