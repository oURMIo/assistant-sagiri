package com.dach.sagiri.bot.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;

/**
 * Represents a callback handler for Telegram bot interactions.
 * <p>
 * Implementations of this interface define how to handle a specific callback query,
 * usually triggered by pressing an inline button in a Telegram chat.
 */
public interface BotCallback {

    /**
     * Returns the unique callback identifier associated with this handler.
     * <p>
     * This identifier is typically compared to the {@code CallbackQuery.data} field
     * to determine which handler should process the incoming callback.
     *
     * @return the callback identifier string
     */
    String callback();

    /**
     * Executes the logic associated with this callback.
     * <p>
     * This method is called when a {@link CallbackQuery} matching this handler is received.
     * It allows sending a response to the user or modifying messages as needed.
     *
     * @param bot      the {@link TelegramBot} instance used to perform actions (like sending messages)
     * @param callback the {@link CallbackQuery} that triggered this execution
     */
    void execute(TelegramBot bot, CallbackQuery callback);
}
