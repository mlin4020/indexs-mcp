package com.indexs.mcp.controller;

import com.indexs.mcp.model.ToolDefinition;
import com.indexs.mcp.service.ToolExecutionService;
import com.indexs.mcp.service.ToolLoaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final ToolLoaderService toolLoaderService;
    private final ToolExecutionService toolExecutionService;

    public McpController(ToolLoaderService toolLoaderService,
                         ToolExecutionService toolExecutionService) {
        this.toolLoaderService = toolLoaderService;
        this.toolExecutionService = toolExecutionService;
    }

    @GetMapping("/tools")
    public ResponseEntity<Map<String, ToolDefinition>> getTools() {
        return ResponseEntity.ok(toolLoaderService.getAllTools());
    }

    @PostMapping("/{toolName}")
    public Mono<ResponseEntity<Object>> executeTool(
            @PathVariable String toolName,
            @RequestBody Map<String, Object> parameters) {
        return toolExecutionService.executeTool(toolName, parameters)
                .map(ResponseEntity::ok)
                .onErrorResume(this::handleError);
    }

    private Mono<ResponseEntity<Object>> handleError(Throwable error) {
        if (error instanceof IllegalArgumentException) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return Mono.just(ResponseEntity.internalServerError()
                .body(Map.of("error", error.getMessage())));
    }
}
