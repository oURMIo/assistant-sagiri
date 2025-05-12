package com.dach.sagiri.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.dach.sagiri.bot.command.BotCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandServiceTest {

    // Initialization with a list of BotCommand objects populates the commandMap correctly
    @Test
    void test_initialization_populates_command_map() {
        BotCommand command1 = mock(BotCommand.class);
        BotCommand command2 = mock(BotCommand.class);
        when(command1.command()).thenReturn("/test1");
        when(command2.command()).thenReturn("/test2");

        List<BotCommand> commands = Arrays.asList(command1, command2);

        CommandService commandService = new CommandService(commands);

        Optional<BotCommand> result1 = commandService.getCommand("/test1");
        Optional<BotCommand> result2 = commandService.getCommand("/test2");

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(command1, result1.get());
        assertEquals(command2, result2.get());
    }

    // getCommand returns empty Optional when command is not found
    @Test
    void test_get_command_returns_empty_when_not_found() {
        BotCommand command = mock(BotCommand.class);
        when(command.command()).thenReturn("/known");

        List<BotCommand> commands = Collections.singletonList(command);
        CommandService commandService = new CommandService(commands);

        Optional<BotCommand> result = commandService.getCommand("/unknown");

        assertFalse(result.isPresent());
    }
}
