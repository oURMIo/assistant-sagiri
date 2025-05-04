package com.dach.sagiri.bot.callback;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.dach.sagiri.property.dto.Project;
import com.dach.sagiri.service.UrlService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class ProjectListCallback implements BotCallback {

    private final UrlService urlService;

    public ProjectListCallback(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public String callback() {
        return "project_list";
    }

    @Override
    public void execute(TelegramBot bot, CallbackQuery callback) {
        Optional<List<Project>> usefulUrlsOptional = urlService.getProjectList();

        if (usefulUrlsOptional.isEmpty()) {
            bot.execute(new SendMessage(callback.from().id(), "Can't find list of project இ௰இ"));
            return;
        }

        List<Project> usefulUrls = usefulUrlsOptional.get();
        SendMessage message = new SendMessage(callback.from().id(), "Useful URLs:");

        InlineKeyboardMarkup markup = createInlineKeyboardMarkup(usefulUrls);

        message.replyMarkup(markup);
        bot.execute(message);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(List<Project> usefulUrls) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        usefulUrls.forEach(dto -> {
            InlineKeyboardButton button = new InlineKeyboardButton(dto.name(), dto.url());
            markup.addRow(button);
        });

        return markup;
    }
}
