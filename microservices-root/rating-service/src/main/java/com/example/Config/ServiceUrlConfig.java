package com.example.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "ecommerce.services")
public record ServiceUrlConfig(
        String product, String customer, String order) {
}