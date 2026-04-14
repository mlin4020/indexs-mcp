package com.indexs.mcp.service;

import org.springframework.ai.mcp.server.annotation.McpTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class MetricsTool {

    @Value("${mcp.server.api-base-url}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @McpTool(name = "get_metrics", description = "获取指标平台的指标数据")
    public Map<String, Object> getMetrics(
            String metric_id, 
            String start_time, 
            String end_time, 
            Map<String, Object> filters) {
        
        String url = apiBaseUrl + "/v1/metrics";
        
        Map<String, Object> requestBody = Map.of(
                "metric_id", metric_id,
                "start_time", start_time,
                "end_time", end_time,
                "filters", filters != null ? filters : Map.of()
        );
        
        return restTemplate.postForObject(url, requestBody, Map.class);
    }
}
