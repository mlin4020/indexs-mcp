package com.indexs.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(McpServerConfig config) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(config.getApiBaseUrl());

        config.getHeaders().forEach((key, value) -> {
            if (key.equals(HttpHeaders.CONTENT_TYPE) && value.equals(MediaType.APPLICATION_JSON_VALUE)) {
                builder.defaultHeader(key, value);
            }
        });

        return builder.build();
    }
}
