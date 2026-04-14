package com.indexs.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ToolDefinition {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("api_endpoint")
    private String apiEndpoint;

    @JsonProperty("method")
    private String method;

    @JsonProperty("parameters")
    private ToolParameter parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ToolParameter getParameters() {
        return parameters;
    }

    public void setParameters(ToolParameter parameters) {
        this.parameters = parameters;
    }
}
