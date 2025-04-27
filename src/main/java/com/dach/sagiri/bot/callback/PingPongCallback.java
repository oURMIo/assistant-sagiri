package com.dach.sagiri.bot.callback;

import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class PingPongCallback implements BotCallback {

    @Override
    public String callback() {
        return "ping";
    }

    @Override
    public void execute(TelegramBot bot, CallbackQuery callback) {
        bot.execute(new AnswerCallbackQuery(callback.id()).text("Pong 🏓"));
        bot.execute(new SendMessage(callback.from().id(), "Pong 🏓"));
    }
}
