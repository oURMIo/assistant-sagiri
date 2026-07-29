package com.dach.sagiri.property;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dach.sagiri.property.dto.Project;
import com.dach.sagiri.property.dto.UsefulUrl;
import com.dach.sagiri.service.FileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileProperty {

    private static final Logger logger = LoggerFactory.getLogger(FileProperty.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String USEFUL_URL_FILE_PATH = "useful-url.json";
    private static final String PROJECT_LIST_FILE_PATH = "project-list.json";
    private static final String USERS_FILE_PATH = "users-list.json";
    private static final String ADMIN_CHAT_ID_FILE_PATH = "admin-chat-id.txt";

    public Optional<List<UsefulUrl>> getUsefulUrls() {
        return loadFromJson(USEFUL_URL_FILE_PATH, new TypeReference<>() {
        });
    }

    public Optional<List<Project>> getProjectList() {
        return loadFromJson(PROJECT_LIST_FILE_PATH, new TypeReference<>() {
        });
    }

    public void addUser(String username) {
        File file = new File(USERS_FILE_PATH);
        try {
            List<String> users;
            if (file.exists()) {
                users = mapper.readValue(file, new TypeReference<>() {
                });
            } else {
                users = new ArrayList<>();
            }
            if (users.contains(username)) {
                return;
            }
            users.add(username);
            mapper.writeValue(file, users);
        } catch (Exception e) {
            logger.error("Failed to add user '{}' to '{}': {}", username, USERS_FILE_PATH, e.getMessage(), e);
        }
    }

    public void saveAdminChatId(long chatId) {
        if (getAdminChatId().filter(saved -> saved == chatId).isPresent()) {
            return;
        }
        try {
            Files.writeString(Path.of(ADMIN_CHAT_ID_FILE_PATH), String.valueOf(chatId));
        } catch (Exception e) {
            logger.error("Failed to save admin chatId '{}' to '{}': {}",
                chatId, ADMIN_CHAT_ID_FILE_PATH, e.getMessage(), e);
        }
    }

    public Optional<Long> getAdminChatId() {
        Path path = Path.of(ADMIN_CHAT_ID_FILE_PATH);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(Files.readString(path).trim()));
        } catch (Exception e) {
            logger.error("Failed to read admin chatId from '{}': {}",
                ADMIN_CHAT_ID_FILE_PATH, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private <T> Optional<List<T>> loadFromJson(String path, TypeReference<List<T>> typeRef) {
        try (InputStream is = FileService.class.getClassLoader().getResourceAsStream(path)) {
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
