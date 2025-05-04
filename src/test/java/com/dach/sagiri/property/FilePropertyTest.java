package com.dach.sagiri.property;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.dach.sagiri.property.dto.UsefulUrl;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class FilePropertyTest {

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
}
