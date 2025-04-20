package com.dach.sagiri.bot.command;

import org.junit.jupiter.api.Test;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PingPongCommandTest {

    // Command returns "/ping" when command() is called
    @Test
    void test_command_returns_ping() {
        PingPongCommand pingPongCommand = new PingPongCommand();

        String result = pingPongCommand.command();

        assertEquals("/ping", result);
    }

    // Handle null TelegramBot parameter in execute method
    @Test
    void test_execute_with_null_bot_throws_exception() {
        PingPongCommand pingPongCommand = new PingPongCommand();
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        assertThrows(NullPointerException.class, () -> {
            pingPongCommand.execute(null, message);
        });
    }
}
