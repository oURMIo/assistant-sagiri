package com.dach.sagiri.bot.command;

import org.springframework.stereotype.Component;
import com.dach.sagiri.property.ClusterProperty;
import com.dach.sagiri.service.WebService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
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
        long chatId = message.chat().id();
        bot.execute(new SendMessage(chatId,
            "I shall now scurry off to inspect the status of the 2 cluster...")
            .replyMarkup(new ReplyKeyboardRemove())
        );

        String resultText = checkServers();
        SendMessage sendMessage = new SendMessage(chatId, resultText)
            .replyMarkup(new ReplyKeyboardRemove());
        bot.execute(sendMessage);
    }

    private String checkServers() {
        boolean isWorking = webService.checkClusterStatus(cluster2Domain);
        return "Cluster 2 status:\n" + (isWorking ? "✅ Available" : "❌ Unavailable");
    }
}
