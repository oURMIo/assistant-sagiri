package com.dach.sagiri.property;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BotConfigPropertyTest {

    // getBotKey returns the value of botKey
    @Test
    void test_get_bot_key_returns_configured_value() {
        // Arrange
        BotConfigProperty botConfigProperty = new BotConfigProperty();
        ReflectionTestUtils.setField(botConfigProperty, "botKey", "test-api-key");

        // Act
        String result = botConfigProperty.getBotKey();

        // Assert
        assertEquals("test-api-key", result);
    }
}
