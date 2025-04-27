package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class HelpCommand implements BotCommand {

    private static final InlineKeyboardMarkup SUPPORTED_BUTTONS = new InlineKeyboardMarkup(
        new InlineKeyboardButton[][]{
            {
                new InlineKeyboardButton("📊 Cluster Status").callbackData("cluster_status"),
                new InlineKeyboardButton("🔗 Useful URLs").callbackData("useful_urls")
            },
            {
                new InlineKeyboardButton("🔔 Notification").callbackData("notification"),
                new InlineKeyboardButton("🌐 Domain Status").callbackData("domain_status")
            },
            {
                new InlineKeyboardButton("🛠️ Project List").callbackData("project_list"),
                new InlineKeyboardButton("🏓 Ping").callbackData("ping")
            },
        }
    );

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        String messageText = "Hello "
                             + message.from().firstName()
                             + ", I provide a list of my capabilities";

        SendMessage request = new SendMessage(message.chat().id(), messageText)
            .replyMarkup(SUPPORTED_BUTTONS);

        bot.execute(request);
    }
}
