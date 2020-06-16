package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

public class CollectionStartRequest extends AbstractDiagnosticsCollectionEvent {

    public CollectionStartRequest(DiagnosticsCollectionEvent event) {
        super(event);
    }

    public CollectionStartRequest(String selector, Long stackId, String accountId, String environmentCrn, String operationId) {
        super(selector, stackId, accountId, environmentCrn, operationId);
    }
}
