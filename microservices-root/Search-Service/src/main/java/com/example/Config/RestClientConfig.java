package com.example.Config;


import co.elastic.clients.elasticsearch.nodes.Http;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.awt.*;

// danh rieng cho ElasticSearch
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
