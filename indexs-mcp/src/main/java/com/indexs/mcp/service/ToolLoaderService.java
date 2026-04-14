package com.indexs.mcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indexs.mcp.model.ToolDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ToolLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ToolLoaderService.class);
    private static final String TOOLS_DIR = "tools";

    private final ObjectMapper objectMapper;
    private final Map<String, ToolDefinition> tools = new HashMap<>();

    public ToolLoaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadTools() {
        File toolsDir = new File(TOOLS_DIR);
        if (!toolsDir.exists() || !toolsDir.isDirectory()) {
            logger.warn("Tools directory not found: {}", TOOLS_DIR);
            return;
        }

        File[] files = toolsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            logger.info("No tool definitions found in {}", TOOLS_DIR);
            return;
        }

        for (File file : files) {
            try {
                ToolDefinition tool = objectMapper.readValue(file, ToolDefinition.class);
                tools.put(tool.getName(), tool);
                logger.info("Loaded tool: {}", tool.getName());
            } catch (IOException e) {
                logger.error("Failed to load tool from file: {}", file.getName(), e);
            }
        }

        logger.info("Total tools loaded: {}", tools.size());
    }

    public ToolDefinition getTool(String name) {
        return tools.get(name);
    }

    public Map<String, ToolDefinition> getAllTools() {
        return new HashMap<>(tools);
    }
}
