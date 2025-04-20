package com.dach.sagiri.service;

import java.net.InetAddress;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebServiceTest {

    // Returns true when a valid domain is reachable
    @Test
    void test_returns_true_when_domain_is_reachable() throws Exception {
        WebService webService = new WebService();
        String validDomain = "localhost";

        InetAddress mockAddress = mock(InetAddress.class);

        try (MockedStatic<InetAddress> mockedStatic = mockStatic(InetAddress.class)) {
            mockedStatic.when(() -> InetAddress.getByName(validDomain)).thenReturn(mockAddress);
            when(mockAddress.isReachable(anyInt())).thenReturn(true);

            boolean result = webService.checkClusterStatus(validDomain);

            assertTrue(result);
            mockedStatic.verify(() -> InetAddress.getByName(validDomain));
            verify(mockAddress).isReachable(4000);
        }
    }

    // Returns false when domain is unreachable but valid
    @Test
    void test_returns_false_when_domain_is_unreachable() throws Exception {
        WebService webService = new WebService();
        String validButUnreachableDomain = "unreachable-domain.example";

        InetAddress mockAddress = mock(InetAddress.class);

        try (MockedStatic<InetAddress> mockedStatic = mockStatic(InetAddress.class)) {
            mockedStatic.when(() -> InetAddress.getByName(validButUnreachableDomain)).thenReturn(mockAddress);
            when(mockAddress.isReachable(anyInt())).thenReturn(false);

            boolean result = webService.checkClusterStatus(validButUnreachableDomain);

            assertFalse(result);
            mockedStatic.verify(() -> InetAddress.getByName(validButUnreachableDomain));
            verify(mockAddress).isReachable(4000);
        }
    }
}
