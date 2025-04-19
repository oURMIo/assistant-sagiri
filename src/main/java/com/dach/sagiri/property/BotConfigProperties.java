package com.dach.sagiri.property;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotConfigProperties {

    @Value("${bot.key}")
    private String botKey;

    public String getBotKey() {
        return botKey;
    }
}
