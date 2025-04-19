package com.dach.sagiri.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

@Service
public class BotMessageService {

    private static final Logger logger = LoggerFactory.getLogger(BotMessageService.class);

    private final CommandService commandService;

    @Autowired
    public BotMessageService(CommandService commandService) {
        this.commandService = commandService;
    }

    public void registerListener(TelegramBot bot) {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                Message message = update.message();
                if (message != null && message.text() != null) {
                    try {
                        handleMessage(bot, message);
                    } catch (Exception e) {
                        logger.error("Got exception while execute command for:'{}'.", update.updateId(), e);
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleMessage(TelegramBot bot, Message message) {
        String text = message.text().trim();
        long chatId = message.chat().id();
        logger.info("Received message from chatId:'{}', text:'{}'", chatId, text);

        commandService.getCommand(text)
            .ifPresentOrElse(
                cmd -> cmd.execute(bot, message),
                () -> bot.execute(new SendMessage(message.chat().id(),
                    "I do not understand you. Select a function from the given list /help"))
            );
    }
}
