package com.dach.sagiri.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dach.sagiri.bot.callback.BotCallback;

@Component
public class CallbackService {

    private final Map<String, BotCallback> callbackMap = new HashMap<>();

    @Autowired
    public CallbackService(List<BotCallback> callbacks) {
        for (BotCallback callback : callbacks) {
            callbackMap.put(callback.callback(), callback);
        }
    }

    public Optional<BotCallback> getCommand(String text) {
        String[] parts = text.split("\\s+");
        return Optional.ofNullable(callbackMap.get(parts[0]));
    }
}
