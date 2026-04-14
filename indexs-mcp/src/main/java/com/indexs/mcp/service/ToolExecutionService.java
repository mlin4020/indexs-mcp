package com.indexs.mcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indexs.mcp.config.McpServerConfig;
import com.indexs.mcp.model.ToolDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class ToolExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(ToolExecutionService.class);

    private final ToolLoaderService toolLoaderService;
    private final WebClient webClient;
    private final McpServerConfig config;
    private final ObjectMapper objectMapper;

    public ToolExecutionService(ToolLoaderService toolLoaderService,
                                 WebClient webClient,
                                 McpServerConfig config,
                                 ObjectMapper objectMapper) {
        this.toolLoaderService = toolLoaderService;
        this.webClient = webClient;
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public Mono<Object> executeTool(String toolName, Map<String, Object> parameters) {
        ToolDefinition tool = toolLoaderService.getTool(toolName);
        if (tool == null) {
            return Mono.error(new IllegalArgumentException("Tool not found: " + toolName));
        }

        logger.info("Executing tool: {}", toolName);
        logger.debug("Parameters: {}", parameters);

        String url = tool.getApiEndpoint();
        HttpMethod method = HttpMethod.valueOf(tool.getMethod().toUpperCase());

        return webClient.method(method)
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(parameters)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(config.getTimeout()))
                .doOnSuccess(result -> logger.info("Tool {} executed successfully", toolName))
                .doOnError(error -> logger.error("Tool {} execution failed", toolName, error));
    }
}
