package com.dach.sagiri.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dach.sagiri.bot.command.BotCommand;

@Component
public class CommandService {

    private final Map<String, BotCommand> commandMap = new HashMap<>();

    @Autowired
    public CommandService(List<BotCommand> commands) {
        for (BotCommand command : commands) {
            commandMap.put(command.command(), command);
        }
    }

    public Optional<BotCommand> getCommand(String text) {
        String[] parts = text.split("\\s+");
        return Optional.ofNullable(commandMap.get(parts[0]));
    }
}
