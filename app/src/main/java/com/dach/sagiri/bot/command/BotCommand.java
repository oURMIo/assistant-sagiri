package com.dach.sagiri.bot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

/**
 * Represents a command that can be executed by a Telegram bot.
 * <p>
 * Implementations of this interface define a specific command and the logic
 * to be executed when that command is received by the bot.
 */
public interface BotCommand {

    /**
     * Returns the command keyword associated with this command.
     * <p>
     * This is typically the string that users will type to trigger this command,
     * for example {@code "/start"} or {@code "/help"}.
     *
     * @return the command keyword as a {@link String}
     */
    String command();

    /**
     * Executes the command logic using the provided bot and message context.
     *
     * @param bot     the {@link TelegramBot} instance to interact with the Telegram API
     * @param message the {@link Message} received from the user that triggered this command
     */
    void execute(TelegramBot bot, Message message);
}
