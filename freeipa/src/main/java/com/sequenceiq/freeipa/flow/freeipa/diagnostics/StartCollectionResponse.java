package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

public class StartCollectionResponse extends AbstractDiagnosticsCollectionEvent {
    public StartCollectionResponse(String selector, Long stackId, String accountId, String environmentCrn) {
        super(selector, stackId, accountId, environmentCrn, operation.getOperationId());
    }

    public StartCollectionResponse(DiagnosticsCollectionEvent event) {
        super(event);
    }
}
