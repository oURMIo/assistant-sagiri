package com.dach.sagiri.bot.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelpCommandTest {

    // Command returns "/help" when command() is called
    @Test
    void test_command_returns_help() {
        HelpCommand helpCommand = new HelpCommand();

        String result = helpCommand.command();

        assertEquals("/help", result);
    }
}
