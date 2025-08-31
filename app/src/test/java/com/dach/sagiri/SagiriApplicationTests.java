package com.dach.sagiri;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Application tests are disabled by default")
class SagiriApplicationTests {

    @Test
    void test_application_starts_with_default_configuration() {
        try {
            String[] args = new String[]{"--spring.main.web-application-type=none"};
            SagiriApplication.main(args);
            assertTrue(true, "Application started successfully");
        } catch (Exception e) {
            fail("Application failed to start with default configuration: " + e.getMessage());
        }
    }

    @Test
    void test_application_starts_with_empty_args() {
        try {
            String[] emptyArgs = new String[0];
            SagiriApplication.main(emptyArgs);
            assertTrue(true, "Application started successfully with empty args");
        } catch (Exception e) {
            fail("Application failed to start with empty args: " + e.getMessage());
        }
    }
}
