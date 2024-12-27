package com.shopify.shopifywebhook.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevShipitConfig implements ShipitConfig {

    @Value("${shipit.api.url}")
    private String apiUrl;

    @Value("${shipit.api.email}")
    private String email;

    @Value("${shipit.api.accessToken}")
    private String accessToken;

    @Override
    public String getApiUrl() {
        return apiUrl;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
