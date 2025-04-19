package com.dach.sagiri.bot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public interface BotCommand {
    String command();

    void execute(TelegramBot bot, Message message);
}
