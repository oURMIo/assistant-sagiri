package com.dach.sagiri.bot.callback;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.dach.sagiri.service.UrlService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class UsefulUrlCallback implements BotCallback {

    private final UrlService urlService;

    public UsefulUrlCallback(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public String callback() {
        return "useful_urls";
    }

    @Override
    public void execute(TelegramBot bot, CallbackQuery callback) {
        Map<UrlService.UrlType, String> urlMap = urlService.getAllUrls();

        SendMessage message = new SendMessage(callback.from().id(), "Useful URLs:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
            new InlineKeyboardButton("Domain Manager", urlMap.get(UrlService.UrlType.DOMAIN_MANAGER)),
            new InlineKeyboardButton("Family Drive", urlMap.get(UrlService.UrlType.GOOGLE_DRIVE_FAMILY))
        );

        message.replyMarkup(markup);
        bot.execute(message);
    }
}
