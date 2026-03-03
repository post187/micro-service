package com.example.Config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
@Data
@Builder
public class ElasticSearchConfig {
    private String url;
    private String username;
    private String password;
}
