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

class ProjectListCallbackTest {

    // Returns "project_list" as the callback identifier
    @Test
    void test_callback_returns_project_list() {
        FileService fileService = mock(FileService.class);
        ProjectListCallback projectListCallback = new ProjectListCallback(fileService);

        String callbackIdentifier = projectListCallback.callback();

        assertEquals("project_list", callbackIdentifier);
    }

    // Handles empty Optional from urlService.getProjectList()
    @Test
    void test_execute_handles_empty_project_list() {
        FileService fileService = mock(FileService.class);
        TelegramBot bot = mock(TelegramBot.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(callbackQuery.from()).thenReturn(user);
        when(user.id()).thenReturn(123456789L);
        when(fileService.getProjectList()).thenReturn(Optional.empty());

        ProjectListCallback projectListCallback = new ProjectListCallback(fileService);

        projectListCallback.execute(bot, callbackQuery);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(messageCaptor.capture());

        SendMessage capturedMessage = messageCaptor.getValue();
        assertEquals(123456789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals("Can't find list of project இ௰இ", capturedMessage.getParameters().get("text"));
    }
}
