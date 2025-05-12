package com.dach.sagiri.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotMessageServiceTest {

    // Successful registration of listener with TelegramBot
    @Test
    void test_register_listener_successfully() {
        CommandService commandService = mock(CommandService.class);
        CallbackService callbackService = mock(CallbackService.class);
        BotMessageService botMessageService = new BotMessageService(commandService, callbackService);
        TelegramBot telegramBot = mock(TelegramBot.class);

        botMessageService.registerListener(telegramBot);

        verify(telegramBot).setUpdatesListener(any(UpdatesListener.class));
    }

    // Handling null message in update
    @Test
    void test_handle_null_message_in_update() {
        CommandService commandService = mock(CommandService.class);
        CallbackService callbackService = mock(CallbackService.class);
        BotMessageService botMessageService = new BotMessageService(commandService, callbackService);
        TelegramBot telegramBot = mock(TelegramBot.class);

        ArgumentCaptor<UpdatesListener> listenerCaptor = ArgumentCaptor.forClass(UpdatesListener.class);
        botMessageService.registerListener(telegramBot);
        verify(telegramBot).setUpdatesListener(listenerCaptor.capture());

        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);
        List<Update> updates = List.of(update);

        int result = listenerCaptor.getValue().process(updates);

        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
        verify(commandService, never()).getCommand(anyString());
    }
}
