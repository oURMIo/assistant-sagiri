package com.dach.sagiri.service;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import com.dach.sagiri.property.UrlProperty;

@Service
public class UrlService {

    private final UrlProperty urlProperty;

    public UrlService(UrlProperty urlProperty) {
        this.urlProperty = urlProperty;
    }

    @NotNull
    public String getDomainManagerUrl() {
        return urlProperty.getDomainManager();
    }

    @NotNull
    public String getGoogleDriveFamilyUrl() {
        return urlProperty.getGoogleDriveFamily();
    }

    public Map<UrlType, String> getAllUrls() {
        return Map.of(
            UrlType.DOMAIN_MANAGER, getDomainManagerUrl(),
            UrlType.GOOGLE_DRIVE_FAMILY, getGoogleDriveFamilyUrl()
        );
    }

    public enum UrlType {
        DOMAIN_MANAGER,
        GOOGLE_DRIVE_FAMILY
    }
}
