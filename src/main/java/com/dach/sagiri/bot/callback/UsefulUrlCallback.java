package com.dach.sagiri.bot.callback;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.dach.sagiri.property.dto.UsefulUrl;
import com.dach.sagiri.service.FileService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class UsefulUrlCallback implements BotCallback {

    private final FileService fileService;

    public UsefulUrlCallback(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public String callback() {
        return "useful_urls";
    }

    @Override
    public void execute(TelegramBot bot, CallbackQuery callback) {
        Optional<List<UsefulUrl>> usefulUrlsOptional = fileService.getUsefulUrls();

        if (usefulUrlsOptional.isEmpty()) {
            bot.execute(new SendMessage(callback.from().id(), "Can't find useful urls இ௰இ"));
            return;
        }

        List<UsefulUrl> usefulUrls = usefulUrlsOptional.get();
        SendMessage message = new SendMessage(callback.from().id(), "Useful URLs:");

        InlineKeyboardMarkup markup = createInlineKeyboardMarkup(usefulUrls);

        message.replyMarkup(markup);
        bot.execute(message);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(List<UsefulUrl> usefulUrls) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        usefulUrls.forEach(dto -> {
            InlineKeyboardButton button = new InlineKeyboardButton(dto.name(), dto.url());
            markup.addRow(button);
        });

        return markup;
    }
}
