package com.dach.sagiri.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClusterProperty {

    @Value("${cluster.1.domain}")
    private String cluster1Domain;

    @Value("${cluster.2.domain}")
    private String cluster2Domain;

    public String getCluster1Domain() {
        return cluster1Domain;
    }

    public String getCluster2Domain() {
        return cluster2Domain;
    }
}
