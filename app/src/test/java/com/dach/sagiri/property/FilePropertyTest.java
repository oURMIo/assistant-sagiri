package com.dach.sagiri.property;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.dach.sagiri.property.dto.UsefulUrl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class FilePropertyTest {

    private static final File USERS_FILE = new File("users-list.json");
    private static final File ADMIN_CHAT_ID_FILE = new File("admin-chat-id.txt");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    void removeUsersFileBefore() {
        USERS_FILE.delete();
        ADMIN_CHAT_ID_FILE.delete();
    }

    @AfterEach
    void removeUsersFileAfter() {
        USERS_FILE.delete();
        ADMIN_CHAT_ID_FILE.delete();
    }

    // Successfully loads and parses useful URLs from the JSON file
    @Test
    void test_get_useful_urls_success() {
        FileProperty fileProperty = new FileProperty();

        Optional<List<UsefulUrl>> result = fileProperty.getUsefulUrls();

        assertTrue(result.isPresent());
        assertFalse(result.get().isEmpty());
        UsefulUrl firstUrl = result.get().get(0);
        assertNotNull(firstUrl.name());
        assertNotNull(firstUrl.url());
        assertNotNull(firstUrl.description());
    }

    // Returns Optional.empty() when resource file is not found in classpath
    @Test
    void test_returns_empty_when_resource_not_found() throws Exception {
        FileProperty filePropertySpy = spy(new FileProperty());
        ClassLoader mockClassLoader = mock(ClassLoader.class);

        when(mockClassLoader.getResourceAsStream(anyString())).thenReturn(null);

        Method loadFromJsonMethod = FileProperty.class.getDeclaredMethod("loadFromJson", String.class, TypeReference.class);
        loadFromJsonMethod.setAccessible(true);

        Optional<List<UsefulUrl>> result = (Optional<List<UsefulUrl>>) loadFromJsonMethod.invoke(
            filePropertySpy,
            "non-existent-file.json",
            new TypeReference<List<UsefulUrl>>() {
            }
        );

        assertFalse(result.isPresent());
    }

    // addUser creates the users file when it does not exist yet
    @Test
    void test_add_user_creates_file_with_single_user() throws Exception {
        FileProperty fileProperty = new FileProperty();

        fileProperty.addUser("stranger");

        assertTrue(USERS_FILE.exists());
        assertEquals(List.of("stranger"), readUsers());
    }

    // addUser appends a new user to the already existing list
    @Test
    void test_add_user_appends_to_existing_list() throws Exception {
        FileProperty fileProperty = new FileProperty();

        fileProperty.addUser("first");
        fileProperty.addUser("second");

        assertEquals(List.of("first", "second"), readUsers());
    }

    // addUser does not store the same user twice
    @Test
    void test_add_user_ignores_duplicates() throws Exception {
        FileProperty fileProperty = new FileProperty();

        fileProperty.addUser("stranger");
        fileProperty.addUser("stranger");

        assertEquals(List.of("stranger"), readUsers());
    }

    // addUser swallows parse errors instead of propagating them
    @Test
    void test_add_user_swallows_broken_file() throws Exception {
        Files.writeString(USERS_FILE.toPath(), "not a json array");
        FileProperty fileProperty = new FileProperty();

        fileProperty.addUser("stranger");

        assertEquals("not a json array", Files.readString(USERS_FILE.toPath()));
    }

    // Admin chatId is unknown while the file does not exist
    @Test
    void test_get_admin_chat_id_returns_empty_when_file_is_absent() {
        FileProperty fileProperty = new FileProperty();

        assertTrue(fileProperty.getAdminChatId().isEmpty());
    }

    // Saved admin chatId is read back from the file
    @Test
    void test_save_and_get_admin_chat_id() {
        FileProperty fileProperty = new FileProperty();

        fileProperty.saveAdminChatId(12345L);

        assertTrue(ADMIN_CHAT_ID_FILE.exists());
        assertEquals(Optional.of(12345L), fileProperty.getAdminChatId());
    }

    // Newer chatId replaces the previously stored one
    @Test
    void test_save_admin_chat_id_overwrites_previous_value() {
        FileProperty fileProperty = new FileProperty();

        fileProperty.saveAdminChatId(1L);
        fileProperty.saveAdminChatId(2L);

        assertEquals(Optional.of(2L), fileProperty.getAdminChatId());
    }

    // Broken file content is swallowed and reported as unknown chatId
    @Test
    void test_get_admin_chat_id_swallows_broken_file() throws Exception {
        Files.writeString(ADMIN_CHAT_ID_FILE.toPath(), "not a number");
        FileProperty fileProperty = new FileProperty();

        assertTrue(fileProperty.getAdminChatId().isEmpty());
    }

    private List<String> readUsers() throws Exception {
        return MAPPER.readValue(USERS_FILE, new TypeReference<List<String>>() {
        });
    }
}
