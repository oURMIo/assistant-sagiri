package com.dach.sagiri.service;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dach.sagiri.property.BotConfigProperty;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

@Service
public class BotMessageService {

    private static final Logger logger = LoggerFactory.getLogger(BotMessageService.class);

    private final CommandService commandService;
    private final CallbackService callbackService;
    private final String adminName;
    private final FileService fileService;

    @Autowired
    public BotMessageService(BotConfigProperty botConfigProperty,
        CommandService commandService,
        CallbackService callbackService,
        FileService fileService) {
        this.adminName = botConfigProperty.getBotAdmin();
        this.commandService = commandService;
        this.callbackService = callbackService;
        this.fileService = fileService;
    }

    public void registerListener(TelegramBot bot) {

        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                processCallback(bot, update);
                processMessage(bot, update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processCallback(TelegramBot bot, Update update) {
        CallbackQuery callback = update.callbackQuery();
        if (ObjectUtils.isEmpty(callback) || StringUtils.isBlank(callback.data())) {
            return;
        }

        long chatId = callback.from().id();
        String username = callback.from().username();

        if (!checkAdminMessage(username)) {
            fileService.updateBlackList(username);
            return;
        }
        fileService.saveAdminChatId(chatId);

        try {
            handleCallBack(bot, callback);
        } catch (Exception e) {
            logger.error("Got exception while execute callback for updateId:'{}', chatId:'{}'.",
                update.updateId(), chatId, e);
        }
    }

    private void processMessage(TelegramBot bot, Update update) {
        Message message = update.message();
        if (ObjectUtils.isNotEmpty(message) && StringUtils.isNotBlank(message.text())) {
            String username = message.from().username();

            if (!checkAdminMessage(username)) {
                fileService.updateBlackList(username);
                return;
            }
            fileService.saveAdminChatId(message.chat().id());

            try {
                handleMessage(bot, message);
            } catch (Exception e) {
                logger.error("Got exception while execute command for updateId:'{}', chatId:'{}'.",
                    update.updateId(), message.from().id(), e);
            }
        }
    }

    private void handleMessage(TelegramBot bot, Message message) {
        String text = message.text().trim();
        long chatId = message.chat().id();
        logger.info("Received message from chatId:'{}', text:'{}'", chatId, text);

        commandService.getCommand(text)
            .ifPresentOrElse(
                cmd -> cmd.execute(bot, message),
                () -> bot.execute(new SendMessage(chatId,
                    "I do not understand you. Select a function from the given list /help"))
            );
    }

    private void handleCallBack(TelegramBot bot, CallbackQuery callback) {
        String data = callback.data();
        long chatId = callback.from().id();
        logger.info("Received callback from chatId:'{}', text:'{}'", chatId, data);

        callbackService.getCommand(data)
            .ifPresentOrElse(
                cmd -> cmd.execute(bot, callback),
                () -> bot.execute(new SendMessage(chatId,
                    "I do not understand you. Select a function from the given list /help"))
            );
    }

    private boolean checkAdminMessage(String username) {
        return !username.isBlank() && adminName.equals(username);
    }
}
