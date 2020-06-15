package com.sequenceiq.freeipa.api.v1.diagnostics.model;

import java.util.Map;

public class LogCollectionRequest {

    private final Map<String, String> parameters;

    private final String environmentCrn;

    public LogCollectionRequest(Map<String, String> parameters, String environmentCrn) {
        this.parameters = parameters;
        this.environmentCrn = environmentCrn;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getEnvironmentCrn() {
        return environmentCrn;
    }
}
