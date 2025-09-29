package com.dach.sagiri.bot.callback;

import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class ClusterStatusCallback implements BotCallback {

    @Override
    public String callback() {
        return "cluster_status";
    }

    @Override
    public void execute(TelegramBot bot, CallbackQuery callback) {
        long chatId = callback.from().id();
        KeyboardButton button1 = new KeyboardButton("/status_cluster1");
        KeyboardButton button2 = new KeyboardButton("/status_cluster2");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(
            button1, button2);
        keyboard.resizeKeyboard(true);

        SendMessage message = new SendMessage(chatId,
            "Please select a cluster so that I may check its status for you")
            .replyMarkup(keyboard);
        bot.execute(message);
    }
}
