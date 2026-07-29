package com.dach.sagiri.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.dach.sagiri.property.FileProperty;
import com.dach.sagiri.property.dto.UsefulUrl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceTest {

    // getUsefulUrls returns Optional with list of UsefulUrl objects when file exists
    @Test
    void test_get_useful_urls_returns_list_when_file_exists() {
        FileProperty mockFileProperty = Mockito.mock(FileProperty.class);
        List<UsefulUrl> expectedUrls = List.of(
            new UsefulUrl("Google", "https://google.com", "Search engine"),
            new UsefulUrl("GitHub", "https://github.com", "Code repository")
        );
        Mockito.when(mockFileProperty.getUsefulUrls()).thenReturn(Optional.of(expectedUrls));

        FileService fileService = new FileService(mockFileProperty);

        Optional<List<UsefulUrl>> result = fileService.getUsefulUrls();

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals("Google", result.get().get(0).name());
        assertEquals("https://google.com", result.get().get(0).url());
        assertEquals("Search engine", result.get().get(0).description());
    }

    // getUsefulUrls returns empty Optional when useful-url.json file is not found
    @Test
    void test_get_useful_urls_returns_empty_when_file_not_found() {
        FileProperty mockFileProperty = Mockito.mock(FileProperty.class);
        Mockito.when(mockFileProperty.getUsefulUrls()).thenReturn(Optional.empty());

        FileService fileService = new FileService(mockFileProperty);

        Optional<List<UsefulUrl>> result = fileService.getUsefulUrls();

        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
    }

    // updateBlackList delegates the username to FileProperty.addUser
    @Test
    void test_update_black_list_delegates_to_file_property() {
        FileProperty mockFileProperty = Mockito.mock(FileProperty.class);
        FileService fileService = new FileService(mockFileProperty);

        fileService.updateBlackList("stranger");

        Mockito.verify(mockFileProperty).addUser("stranger");
    }

    // saveAdminChatId delegates the chatId to FileProperty
    @Test
    void test_save_admin_chat_id_delegates_to_file_property() {
        FileProperty mockFileProperty = Mockito.mock(FileProperty.class);
        FileService fileService = new FileService(mockFileProperty);

        fileService.saveAdminChatId(100L);

        Mockito.verify(mockFileProperty).saveAdminChatId(100L);
    }

    // getAdminChatId returns the value stored by FileProperty
    @Test
    void test_get_admin_chat_id_returns_stored_value() {
        FileProperty mockFileProperty = Mockito.mock(FileProperty.class);
        Mockito.when(mockFileProperty.getAdminChatId()).thenReturn(Optional.of(100L));

        FileService fileService = new FileService(mockFileProperty);

        assertEquals(Optional.of(100L), fileService.getAdminChatId());
    }
}
