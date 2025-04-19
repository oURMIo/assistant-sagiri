package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class StartCommand implements BotCommand {

    private static final String START_MESSAGE_TEXT = """
        I am Sagiri, your humble assistant bot.
        I am here to assist you by diligently checking the status of our servers and promptly notifying you of their condition""";

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        String startMessage = "Greetings, " + message.from().firstName() + "\n" + START_MESSAGE_TEXT;
        bot.execute(new SendMessage(message.chat().id(), startMessage));
    }
}
