package com.dach.sagiri.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import com.dach.sagiri.domain.dto.nager.HolidayNagerDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebServiceTest {

    private WebService webService;
    private MockedConstruction<Socket> mockedSocket;
    private HttpClient httpClient;
    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        webService = new WebService();
    }

    @AfterEach
    void tearDown() {
        if (mockedSocket != null) {
            mockedSocket.close();
        }
    }

    @Test
    void test_returns_true_when_socket_connects_successfully() throws Exception {
        String host = "example.org";

        mockedSocket = mockConstruction(Socket.class);

        boolean result = webService.checkClusterStatus(host);

        assertTrue(result, "Expected true when socket.connect succeeds");

        Socket constructedSocket = mockedSocket.constructed().getFirst();
        ArgumentCaptor<SocketAddress> addressCaptor = ArgumentCaptor.forClass(SocketAddress.class);
        ArgumentCaptor<Integer> timeoutCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(constructedSocket).connect(addressCaptor.capture(), timeoutCaptor.capture());
        verify(constructedSocket).close();

        SocketAddress addr = addressCaptor.getValue();
        int timeout = timeoutCaptor.getValue();

        InetSocketAddress inet = (InetSocketAddress) addr;
        assertEquals(host, inet.getHostString());
        assertEquals(22, inet.getPort());
        assertEquals(4000, timeout);
    }

    @Test
    void test_returns_false_when_socket_connect_throws_exception() throws Exception {
        String host = "unreachable.example";

        mockedSocket = mockConstruction(Socket.class, (mock, context) -> doThrow(new IOException("Connection failed"))
            .when(mock)
            .connect(any(SocketAddress.class), anyInt()));

        boolean result = webService.checkClusterStatus(host);

        assertFalse(result, "Expected false when socket.connect throws an exception");

        Socket constructedSocket = mockedSocket.constructed().getFirst();
        verify(constructedSocket).close();
    }

    @Test
    void testDoRequestForList() {
        String url = "https://date.nager.at/api/v3/PublicHolidays/2025/RU";

        mockedSocket = mockConstruction(Socket.class, (mock, context) -> doThrow(new IOException("Connection failed"))
            .when(mock)
            .connect(any(SocketAddress.class), anyInt()));

        List<HolidayNagerDto> result = webService.doRequestForList(url, HolidayNagerDto.class);

        assertNotEquals(null, result, "Expected non-null result when socket.connect returns false");
        assertFalse(result.isEmpty(), "Expected non-empty list");

        for (HolidayNagerDto dto : result) {
            assertNotEquals(null, dto, "DTO must not be null");
            assertNotEquals(null, dto.date(), "date must be parsed");
            assertNotEquals(null, dto.name(), "name must be parsed");
            assertNotEquals(null, dto.countryCode(), "countryCode must be parsed");
            assertFalse(dto.name().isBlank(), "name must not be blank");
            assertFalse(dto.countryCode().isBlank(), "countryCode must not be blank");

            System.out.printf("dto: %s%n", dto);
        }
    }

    @Test
    void doRequestForList_returns_empty_when_url_or_class_invalid() {
        assertTrue(webService.doRequestForList(null, HolidayNagerDto.class).isEmpty());
        assertTrue(webService.doRequestForList("", HolidayNagerDto.class).isEmpty());
        assertTrue(webService.doRequestForList("   ", HolidayNagerDto.class).isEmpty());
        assertTrue(webService.doRequestForList("https://example.org", null).isEmpty());
    }

    @Test
    void doRequestForList_parses_valid_json_and_empty_on_malformed() throws Exception {
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("""
            [
              {
                "date":"2025-01-01",
                "localName":"Новый год",
                "name":"New Year",
                "countryCode":"RU",
                "fixed":true,
                "global":true,
                "counties":null,
                "launchYear":1992,
                "types":["Public"]
              },
              {
                "date":"2025-01-07",
                "localName":"Рождество",
                "name":"Christmas",
                "countryCode":"RU",
                "fixed":false,
                "global":true,
                "counties":null,
                "launchYear":null,
                "types":["Public"]
              }
            ]
            """);

        try (var mockedBuilder = org.mockito.Mockito.mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);
            mockedBuilder.when(HttpClient::newBuilder).thenReturn(builder);
            when(builder.connectTimeout(any())).thenReturn(builder);
            when(builder.build()).thenReturn(httpClient);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

            webService = new WebService();

            List<HolidayNagerDto> list =
                webService.doRequestForList("https://example.org/ok", HolidayNagerDto.class);
            assertEquals(2, list.size());
            assertEquals("New Year", list.get(0).name());
            assertEquals("RU", list.get(0).countryCode());
        }

        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{ invalid [ json }");

        try (var mockedBuilder = org.mockito.Mockito.mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);
            mockedBuilder.when(HttpClient::newBuilder).thenReturn(builder);
            when(builder.connectTimeout(any())).thenReturn(builder);
            when(builder.build()).thenReturn(httpClient);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

            webService = new WebService();

            List<HolidayNagerDto> list =
                webService.doRequestForList("https://example.org/bad", HolidayNagerDto.class);
            assertTrue(list.isEmpty());
        }
    }

    @Test
    void doRequest_returns_empty_when_url_null_or_blank() {
        assertTrue(webService.doRequest(null).isEmpty());
        assertTrue(webService.doRequest("").isEmpty());
        assertTrue(webService.doRequest("   ").isEmpty());
    }

    @Test
    void doRequest_returns_empty_on_invalid_url() {
        assertTrue(webService.doRequest("ht!tp://bad\\url").isEmpty());
    }

    @Test
    void doRequest_returns_body_on_2xx() throws Exception {
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"ok\":true}");

        try (var mockedBuilder = org.mockito.Mockito.mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);

            mockedBuilder.when(HttpClient::newBuilder).thenReturn(builder);
            when(builder.connectTimeout(any())).thenReturn(builder);
            when(builder.build()).thenReturn(httpClient);

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

            webService = new WebService();

            Optional<String> result = webService.doRequest("https://example.org/api");

            assertTrue(result.isPresent());
            assertEquals("{\"ok\":true}", result.get());
        }
    }

    @Test
    void doRequest_returns_empty_on_non_2xx() throws Exception {
        webService = new WebService();

        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("{\"error\":\"not found\"}");

        try (var mockedBuilder = org.mockito.Mockito.mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);

            mockedBuilder.when(HttpClient::newBuilder).thenReturn(builder);
            when(builder.connectTimeout(any())).thenReturn(builder);
            when(builder.build()).thenReturn(httpClient);

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

            Optional<String> result = webService.doRequest("https://example.org/missing");

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void doRequest_returns_empty_on_io_exception() throws Exception {
        webService = new WebService();

        httpClient = mock(HttpClient.class);

        try (var mockedBuilder = org.mockito.Mockito.mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);

            mockedBuilder.when(HttpClient::newBuilder).thenReturn(builder);
            when(builder.connectTimeout(any())).thenReturn(builder);
            when(builder.build()).thenReturn(httpClient);

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("boom"));

            Optional<String> result = webService.doRequest("https://example.org/ioe");

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void doRequest_returns_empty_on_interrupted_exception_and_sets_interrupt_flag() throws Exception {
        httpClient = mock(HttpClient.class);

        boolean wasInterruptedBefore = Thread.currentThread().isInterrupted();
        try (var mockedBuilder = org.mockito.Mockito.mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);

            mockedBuilder.when(HttpClient::newBuilder).thenReturn(builder);
            when(builder.connectTimeout(any())).thenReturn(builder);
            when(builder.build()).thenReturn(httpClient);

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenAnswer(invocation -> {
                    HttpRequest req = invocation.getArgument(0);
                    assertEquals(URI.create("https://example.org/interrupt"), req.uri());
                    assertEquals("application/json", req.headers().firstValue("Accept").orElse(null));
                    assertEquals(java.nio.charset.StandardCharsets.UTF_8.name(),
                        req.headers().firstValue("Accept-Charset").orElse(null));
                    throw new InterruptedException("stop");
                });

            webService = new WebService();

            Optional<String> result = webService.doRequest("https://example.org/interrupt");

            assertTrue(result.isEmpty());
            assertTrue(Thread.currentThread().isInterrupted(), "interrupt flag should be set");

            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpClient, never()).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        } finally {
            if (!wasInterruptedBefore && Thread.currentThread().isInterrupted()) {
                Thread.interrupted();
            }
        }
    }
}
