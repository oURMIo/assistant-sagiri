package com.dach.sagiri.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.dach.sagiri.bot.callback.BotCallback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CallbackServiceTest {

    // Callbacks are correctly registered in the map during initialization
    @Test
    void test_callbacks_are_registered_during_initialization() {
        BotCallback mockCallback1 = mock(BotCallback.class);
        BotCallback mockCallback2 = mock(BotCallback.class);

        when(mockCallback1.callback()).thenReturn("callback1");
        when(mockCallback2.callback()).thenReturn("callback2");

        List<BotCallback> callbacks = Arrays.asList(mockCallback1, mockCallback2);

        CallbackService callbackService = new CallbackService(callbacks);

        Optional<BotCallback> result1 = callbackService.getCommand("callback1");
        Optional<BotCallback> result2 = callbackService.getCommand("callback2");

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(mockCallback1, result1.get());
        assertEquals(mockCallback2, result2.get());

        verify(mockCallback1).callback();
        verify(mockCallback2).callback();
    }

    // getCommand returns empty Optional when given an unknown callback identifier
    @Test
    void test_get_command_returns_empty_optional_for_unknown_callback() {
        BotCallback mockCallback = mock(BotCallback.class);
        when(mockCallback.callback()).thenReturn("knownCallback");

        List<BotCallback> callbacks = Collections.singletonList(mockCallback);
        CallbackService callbackService = new CallbackService(callbacks);

        Optional<BotCallback> result = callbackService.getCommand("unknownCallback");

        assertFalse(result.isPresent());
        verify(mockCallback).callback();
    }
}
