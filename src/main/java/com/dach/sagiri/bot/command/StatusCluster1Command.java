package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.dach.sagiri.property.ClusterProperty;
import com.dach.sagiri.service.WebService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class StatusCluster1Command implements BotCommand {

    private final String cluster1Domain;
    private final WebService webService;

    public StatusCluster1Command(WebService webService, ClusterProperty clusterProperty) {
        this.webService = webService;
        cluster1Domain = clusterProperty.getCluster1Domain();
    }

    @Override
    public String command() {
        return "/status_cluster1";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        bot.execute(new SendMessage(message.chat().id(),
            "I shall now scurry off to inspect the status of the 1 cluster...")
            .replyMarkup(new ReplyKeyboardRemove())
        );

        String resultText = checkServers();
        bot.execute(new SendMessage(message.chat().id(), resultText));
    }

    private String checkServers() {
        boolean isWorking = webService.checkClusterStatus(cluster1Domain);
        return "Cluster 1 status:\n" + (isWorking ? "✅ Available" : "❌ Unavailable");
    }
}
