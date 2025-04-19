package com.dach.sagiri.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dach.sagiri.property.BotConfigProperties;
import com.pengrad.telegrambot.TelegramBot;

@Configuration
public class BotConfig {

    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);

    @Bean
    public TelegramBot telegramBot(BotConfigProperties properties) {
        logger.info("Create telegram bot bean");
        return new TelegramBot(properties.getBotKey());
    }
}
