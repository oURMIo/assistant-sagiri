package com.dach.sagiri.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlProperty {

    @Value("${url.domain.manager}")
    private String domainManager;

    @Value("${url.google.drive.family}")
    private String googleDriveFamily;

    public String getDomainManager() {
        return domainManager;
    }

    public String getGoogleDriveFamily() {
        return googleDriveFamily;
    }
}
