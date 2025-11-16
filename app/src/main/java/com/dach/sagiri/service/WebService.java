package com.dach.sagiri.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebService.class);

    private static final int CHECK_STATUS_TIMEOUT_MS = 4000;
    private static final int SSH_PORT = 22;
    private static final int BODY_PREVIEW_LIMIT = 1000;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaTimeModule javaTimeModule = new JavaTimeModule();

    public boolean checkClusterStatus(String clusterDomain) {
        if (clusterDomain == null || clusterDomain.isBlank()) {
            return false;
        }

        boolean isAvailable = testConnection(clusterDomain);
        if (!isAvailable) {
            logger.warn("Cluster '{}' have status unavailable after checking", clusterDomain);
        }

        return isAvailable;
    }

    public <T> List<T> doRequestForList(String url, Class<T> dtoClass) {
        if (url == null || url.isBlank() || dtoClass == null) {
            return List.of();
        }

        Optional<String> jsonResponse = doRequest(url);
        if (jsonResponse.isEmpty() || jsonResponse.get().isBlank()) {
            return List.of();
        }

        String response = jsonResponse.get();
        try {
            objectMapper.registerModule(javaTimeModule);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, dtoClass);

            return objectMapper.readValue(response, listType);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize JSON response to List of class:'{}'", dtoClass.getSimpleName(), e);
            return List.of();
        }
    }

    public Optional<String> doRequest(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid URL provided: {}", url, ex);
            return Optional.empty();
        }

        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(10))
            .header("Accept", "application/json")
            .header("Accept-Charset", StandardCharsets.UTF_8.name())
            .GET()
            .build();

        logger.info("Sending HTTP GET request to {}", uri);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                String body = response.body();
                return Optional.ofNullable(body);
            } else {
                String bodyPreview = truncate(response.body());
                logger.warn("Non-2xx response from {}: status={}, bodyPreview={}", uri, status, bodyPreview);
                return Optional.empty();
            }
        } catch (IOException e) {
            logger.error("I/O error during HTTP request to {}", uri, e);
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("HTTP request to {} was interrupted", uri, e);
            return Optional.empty();
        }
    }

    private static String truncate(String body) {
        if (body == null) {
            return null;
        }
        if (body.length() <= WebService.BODY_PREVIEW_LIMIT) {
            return body;
        }
        return body.substring(0, WebService.BODY_PREVIEW_LIMIT) + "...(truncated)";
    }

    private boolean testConnection(String host) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, SSH_PORT), CHECK_STATUS_TIMEOUT_MS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
