package com.sequenceiq.freeipa.api.v1.diagnostics.model;

import com.sequenceiq.flow.api.model.FlowIdentifier;

public class LogCollectionResponse {

    private final FlowIdentifier flowIdentifier;

    public LogCollectionResponse(FlowIdentifier flowIdentifier) {
        this.flowIdentifier = flowIdentifier;
    }

    public FlowIdentifier getFlowIdentifier() {
        return flowIdentifier;
    }
}
