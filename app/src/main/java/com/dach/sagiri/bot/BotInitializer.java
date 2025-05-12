package com.dach.sagiri.bot;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.dach.sagiri.service.BotMessageService;
import com.pengrad.telegrambot.TelegramBot;

@Component
public class BotInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    private final TelegramBot bot;
    private final BotMessageService botMessageService;

    public BotInitializer(TelegramBot bot, BotMessageService botMessageService) {
        this.bot = bot;
        this.botMessageService = botMessageService;
    }

    @PostConstruct
    public void start() {
        logger.info("Starting Telegram bot Sagiri...");
        botMessageService.registerListener(bot);
    }
}
