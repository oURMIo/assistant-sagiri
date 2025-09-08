package com.dach.sagiri.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

class WebServiceTest {

    @Test
    void test_returns_true_when_socket_connects_successfully() throws Exception {
        WebService webService = new WebService();
        String host = "example.org";

        try (MockedConstruction<Socket> mocked = mockConstruction(Socket.class)) {
            boolean result = webService.checkClusterStatus(host);

            assertTrue(result, "Expected true when socket.connect succeeds");

            Socket constructedSocket = mocked.constructed().getFirst();
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
    }

    @Test
    void test_returns_false_when_socket_connect_throws_exception() throws Exception {
        WebService webService = new WebService();
        String host = "unreachable.example";

        try (MockedConstruction<Socket> mocked = mockConstruction(Socket.class, (mock, context) -> {
            org.mockito.Mockito.doThrow(new IOException("Connection failed"))
                .when(mock)
                .connect(org.mockito.ArgumentMatchers.any(SocketAddress.class), org.mockito.ArgumentMatchers.anyInt());
        })) {
            boolean result = webService.checkClusterStatus(host);

            assertFalse(result, "Expected false when socket.connect throws an exception");

            Socket constructedSocket = mocked.constructed().getFirst();
            verify(constructedSocket).close();
        }
    }
}
