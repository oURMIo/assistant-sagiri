package com.dach.sagiri.bot.callback;

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

        SendMessage sendMessage = switch (data) {
            case "holiday rus" -> sendRusTodayHoliday(chatId);
            case "holiday arm" -> sendArmTodayHoliday(chatId);
            default -> defaultMessage(chatId);
        };

        bot.execute(sendMessage);
    }

    private SendMessage defaultMessage(long chatId) {
        String messageText = "Please select the country you are interested in:";
        return new SendMessage(chatId, messageText)
            .replyMarkup(KEYBOARD);
    }

    private SendMessage sendRusTodayHoliday(long chatId) {
        String messageText = getTodayHoliday(NagerCountry.RU);
        return new SendMessage(chatId, messageText);
    }

    private SendMessage sendArmTodayHoliday(long chatId) {
        String messageText = getTodayHoliday(NagerCountry.AM);
        return new SendMessage(chatId, messageText);
    }

    private String getTodayHoliday(NagerCountry country) {
        Optional<HolidayNagerDto> holiday = holidayService.getTodayHolidays(country);

        return holiday
            .map(holidayNagerDto -> "Today is a holiday: " + holidayNagerDto.name() + ". Congratulations")
            .orElse("Unfortunately, there are no holidays today.");
    }
}
