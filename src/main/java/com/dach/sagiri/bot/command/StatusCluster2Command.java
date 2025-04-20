package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.dach.sagiri.property.ClusterProperty;
import com.dach.sagiri.service.WebService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class StatusCluster2Command implements BotCommand {

    private final String cluster2Domain;
    private final WebService webService;

    public StatusCluster2Command(WebService webService, ClusterProperty clusterProperty) {
        this.webService = webService;
        cluster2Domain = clusterProperty.getCluster2Domain();
    }

    @Override
    public String command() {
        return "/status_cluster2";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        bot.execute(new SendMessage(message.chat().id(),
            "I shall now scurry off to inspect the status of the 2 cluster..."));

        String resultText = checkServers();
        bot.execute(new SendMessage(message.chat().id(), resultText));
    }

    private String checkServers() {
        boolean isWorking = webService.checkClusterStatus(cluster2Domain);
        return "Cluster 2 status:\n" + (isWorking ? "✅ Available" : "❌ Unavailable");
    }
}
