package com.dach.sagiri.bot.callback;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.dach.sagiri.domain.dto.nager.HolidayNagerDto;
import com.dach.sagiri.domain.dto.nager.NagerCountry;
import com.dach.sagiri.service.HolidayService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class HolidayCallback implements BotCallback {

    private static final InlineKeyboardMarkup KEYBOARD = new InlineKeyboardMarkup(
        new InlineKeyboardButton[][]{
            {
                new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFA RUS").callbackData("holiday rus"),
                new InlineKeyboardButton("\uD83C\uDDE6\uD83C\uDDF2 ARM").callbackData("holiday arm")
            },
            {
                new InlineKeyboardButton("🗺️ Global List").callbackData("holiday all")
            }
        }
    );

    private final HolidayService holidayService;

    public HolidayCallback(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @Override
    public String callback() {
        return "holiday";
    }

    @Override
    public void execute(TelegramBot bot, CallbackQuery callback) {
        long chatId = callback.from().id();
        String data = callback.data();

        switch (data) {
            case "holiday rus" -> processRusTodayHoliday(bot, chatId);
            case "holiday arm" -> processArmTodayHoliday(bot, chatId);
            case "holiday all" -> processAllHolidays(bot, chatId);
            default -> processDefaultMessage(bot, chatId);
        }
    }

    private void processDefaultMessage(TelegramBot bot, long chatId) {
        String messageText = "Please select the country you are interested in:";
        SendMessage message = new SendMessage(chatId, messageText)
            .replyMarkup(KEYBOARD);
        bot.execute(message);
    }

    private void processRusTodayHoliday(TelegramBot bot, long chatId) {
        String messageText = getTodayHoliday(NagerCountry.RU);
        SendMessage message = new SendMessage(chatId, messageText);
        bot.execute(message);
    }

    private void processArmTodayHoliday(TelegramBot bot, long chatId) {
        String messageText = getTodayHoliday(NagerCountry.AM);
        SendMessage message = new SendMessage(chatId, messageText);
        bot.execute(message);
    }

    private void processAllHolidays(TelegramBot bot, long chatId) {
        bot.execute(
            new SendMessage(chatId, "I’ll go ahead and gather the data. It may take a bit of time, so please hold on for a moment."));

        List<String> holidays = holidayService.getTodayHolidaysFromKakoySegodnyaPrazdnik();
        StringBuilder messageText = new StringBuilder("Here is a list of today's holidays in many countries:")
            .append("\n")
            .append("\n");
        int index = 1;
        for (String holiday : holidays) {
            messageText
                .append(index++)
                .append(". ")
                .append(holiday)
                .append("\n");
        }
        SendMessage message = new SendMessage(chatId, messageText.toString());
        bot.execute(message);
    }

    private String getTodayHoliday(NagerCountry country) {
        Optional<HolidayNagerDto> holiday = holidayService.getTodayHolidays(country);

        return holiday
            .map(holidayNagerDto -> "Today is a holiday: " + holidayNagerDto.name() + ". Congratulations")
            .orElse("Unfortunately, there are no holidays today.");
    }
}
