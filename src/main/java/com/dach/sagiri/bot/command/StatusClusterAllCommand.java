package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.dach.sagiri.property.ClusterProperty;
import com.dach.sagiri.service.WebService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class StatusClusterAllCommand implements BotCommand {

    private final String cluster1Domain;
    private final String cluster2Domain;
    private final WebService webService;

    public StatusClusterAllCommand(WebService webService, ClusterProperty clusterProperty) {
        this.webService = webService;
        cluster1Domain = clusterProperty.getCluster1Domain();
        cluster2Domain = clusterProperty.getCluster2Domain();
    }

    @Override
    public String command() {
        return "/status_cluster_all";
    }

    @Override
    public void execute(TelegramBot bot, Message message) {
        bot.execute(new SendMessage(message.chat().id(),
            "I shall now scurry off to inspect the status of the clusters..."));

        String resultText = checkServers();
        bot.execute(new SendMessage(message.chat().id(), resultText));
    }

    private String checkServers() {
        boolean is1Working = webService.checkClusterStatus(cluster1Domain);
        boolean is2Working = webService.checkClusterStatus(cluster2Domain);
        return "Status:\n"
               + "Cluster 1: " + (is1Working ? "✅ Available" : "❌ Unavailable") + "\n"
               + "Cluster 2: " + (is2Working ? "✅ Available" : "❌ Unavailable");
    }
}
