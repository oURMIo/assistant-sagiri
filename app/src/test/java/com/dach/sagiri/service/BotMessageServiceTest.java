package com.dach.sagiri.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.dach.sagiri.bot.callback.BotCallback;
import com.dach.sagiri.bot.command.BotCommand;
import com.dach.sagiri.property.BotConfigProperty;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotMessageServiceTest {

    private static final String ADMIN = "admin_user";

    private final CommandService commandService = mock(CommandService.class);
    private final CallbackService callbackService = mock(CallbackService.class);
    private final FileService fileService = mock(FileService.class);

    // Successful registration of listener with TelegramBot
    @Test
    void test_register_listener_successfully() {
        BotMessageService botMessageService = newService();
        TelegramBot telegramBot = mock(TelegramBot.class);

        botMessageService.registerListener(telegramBot);

        verify(telegramBot).setUpdatesListener(any(UpdatesListener.class));
    }

    // Handling null message in update
    @Test
    void test_handle_null_message_in_update() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        int result = process(telegramBot, update);

        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
        verify(commandService, never()).getCommand(anyString());
        verify(fileService, never()).updateBlackList(anyString());
    }

    // Message from the admin is dispatched to the matching command
    @Test
    void test_message_from_admin_is_executed() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = messageUpdate(ADMIN, "/help");
        BotCommand command = mock(BotCommand.class);
        when(commandService.getCommand("/help")).thenReturn(Optional.of(command));

        process(telegramBot, update);

        verify(command).execute(telegramBot, update.message());
        verify(fileService, never()).updateBlackList(anyString());
    }

    // Message from a non-admin is ignored and the sender is blacklisted
    @Test
    void test_message_from_non_admin_is_blacklisted() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = messageUpdate("stranger", "/help");

        process(telegramBot, update);

        verify(fileService).updateBlackList("stranger");
        verify(commandService, never()).getCommand(anyString());
        verify(telegramBot, never()).execute(any());
    }

    // Unknown command from the admin gets the fallback reply
    @Test
    void test_unknown_command_from_admin_gets_fallback_reply() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = messageUpdate(ADMIN, "hello there");
        when(commandService.getCommand("hello there")).thenReturn(Optional.empty());

        process(telegramBot, update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        assertEquals("I do not understand you. Select a function from the given list /help",
            captor.getValue().getParameters().get("text"));
        verify(fileService, never()).updateBlackList(anyString());
    }

    // Callback from the admin is dispatched to the matching callback handler
    @Test
    void test_callback_from_admin_is_executed() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = callbackUpdate(ADMIN, "ping");
        BotCallback callback = mock(BotCallback.class);
        when(callbackService.getCommand("ping")).thenReturn(Optional.of(callback));

        process(telegramBot, update);

        verify(callback).execute(telegramBot, update.callbackQuery());
        verify(fileService, never()).updateBlackList(anyString());
    }

    // Callback from a non-admin is ignored and the sender is blacklisted
    @Test
    void test_callback_from_non_admin_is_blacklisted() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = callbackUpdate("stranger", "ping");

        process(telegramBot, update);

        verify(fileService).updateBlackList("stranger");
        verify(callbackService, never()).getCommand(anyString());
        verify(telegramBot, never()).execute(any());
    }

    // Blank callback data is ignored without blacklisting the sender
    @Test
    void test_blank_callback_data_is_ignored() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = callbackUpdate("stranger", "  ");

        process(telegramBot, update);

        verify(fileService, never()).updateBlackList(anyString());
        verify(callbackService, never()).getCommand(anyString());
    }

    // Exception thrown by a command handler does not break the update loop
    @Test
    void test_command_exception_does_not_break_listener() {
        TelegramBot telegramBot = mock(TelegramBot.class);
        Update update = messageUpdate(ADMIN, "/help");
        BotCommand command = mock(BotCommand.class);
        when(commandService.getCommand("/help")).thenReturn(Optional.of(command));
        doThrow(new RuntimeException("boom")).when(command).execute(any(), any(Message.class));

        int result = process(telegramBot, update);

        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    private BotMessageService newService() {
        BotConfigProperty botConfigProperty = mock(BotConfigProperty.class);
        when(botConfigProperty.getBotAdmin()).thenReturn(ADMIN);
        return new BotMessageService(botConfigProperty, commandService, callbackService, fileService);
    }

    private int process(TelegramBot telegramBot, Update update) {
        BotMessageService botMessageService = newService();
        ArgumentCaptor<UpdatesListener> listenerCaptor = ArgumentCaptor.forClass(UpdatesListener.class);
        botMessageService.registerListener(telegramBot);
        verify(telegramBot).setUpdatesListener(listenerCaptor.capture());
        return listenerCaptor.getValue().process(List.of(update));
    }

    private Update messageUpdate(String username, String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(text);
        when(message.from()).thenReturn(user);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(100L);
        when(user.id()).thenReturn(100L);
        when(user.username()).thenReturn(username);
        return update;
    }

    private Update callbackUpdate(String username, String data) {
        Update update = mock(Update.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(update.callbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.data()).thenReturn(data);
        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(100L);
        when(user.username()).thenReturn(username);
        return update;
    }
}
