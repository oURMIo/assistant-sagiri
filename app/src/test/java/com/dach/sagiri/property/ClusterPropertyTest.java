package com.dach.sagiri.property;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClusterPropertyTest {

    // getCluster1Domain returns the value injected from cluster.1.domain property
    @Test
    void test_get_cluster1_domain_returns_injected_value() {
        ClusterProperty clusterProperty = new ClusterProperty();
        ReflectionTestUtils.setField(clusterProperty, "cluster1Domain", "test-domain-1.example.com");

        String result = clusterProperty.getCluster1Domain();

        assertEquals("test-domain-1.example.com", result);
    }
}
