package com.dach.sagiri.bot.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;

public interface BotCallback {

    String callback();

    void execute(TelegramBot bot, CallbackQuery callback);
}
