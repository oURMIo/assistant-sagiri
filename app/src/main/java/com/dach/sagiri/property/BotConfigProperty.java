package com.dach.sagiri.property;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotConfigProperty {

    @Value("${bot.key}")
    private String botKey;

    @Value("${bot.admin}")
    private String botAdmin;

    public String getBotKey() {
        return botKey;
    }

    public String getBotAdmin() {
        return botAdmin;
    }
}
