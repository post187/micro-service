package com.example.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.Repository")
@ComponentScan(basePackages = "com.example.Repository")
@RequiredArgsConstructor
public class ClientConfig {
    private final ElasticSearchConfig elasticSearchConfig;

    @Bean
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticSearchConfig.getUrl())
                .withBasicAuth(elasticSearchConfig.getUsername(), elasticSearchConfig.getPassword())
                .build();
    }
}
