package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class PingPongCommand implements BotCommand {

    @Override
    public String command() {
        return "/ping";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        bot.execute(new SendMessage(message.chat().id(), "Pong 🏓"));
    }
}
