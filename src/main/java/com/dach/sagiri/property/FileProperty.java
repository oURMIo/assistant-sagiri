package com.dach.sagiri.property;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dach.sagiri.property.dto.Project;
import com.dach.sagiri.property.dto.UsefulUrl;
import com.dach.sagiri.service.UrlService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileProperty {

    private static final Logger logger = LoggerFactory.getLogger(FileProperty.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String USEFUL_URL_FILE_PATH = "useful-url.json";
    private static final String PROJECT_LIST_FILE_PATH = "project-list.json";

    public Optional<List<UsefulUrl>> getUsefulUrls() {
        return loadFromJson(USEFUL_URL_FILE_PATH, new TypeReference<>() {
        });
    }

    public Optional<List<Project>> getProjectList() {
        return loadFromJson(PROJECT_LIST_FILE_PATH, new TypeReference<>() {
        });
    }

    private <T> Optional<List<T>> loadFromJson(String path, TypeReference<List<T>> typeRef) {
        try (InputStream is = UrlService.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                logger.error("Resource file '{}' not found in classpath.", path);
                return Optional.empty();
            }
            return Optional.of(mapper.readValue(is, typeRef));
        } catch (Exception e) {
            logger.error("Failed to load or parse '{}': {}", path, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
