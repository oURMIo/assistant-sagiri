package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class HelpCommand implements BotCommand {

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        String messageText = "Hello "
                             + message.from().firstName()
                             + ", I provide a list of my capabilities";
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
            new InlineKeyboardButton("👍 Yes").callbackData("yes"),
            new InlineKeyboardButton("👎 No").callbackData("no")
        );

        SendMessage request = new SendMessage(message.chat().id(), messageText)
            .replyMarkup(keyboard);
        bot.execute(request);
    }
}
