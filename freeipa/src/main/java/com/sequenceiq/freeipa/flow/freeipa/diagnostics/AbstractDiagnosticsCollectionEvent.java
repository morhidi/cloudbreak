package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

public class AbstractDiagnosticsCollectionEvent extends DiagnosticsCollectionEvent {

    public AbstractDiagnosticsCollectionEvent(String selector, Long stackId, String accountId, String environmentCrn, String operationId) {
        super(selector, stackId, accountId, environmentCrn, operationId);
    }

    public AbstractDiagnosticsCollectionEvent(DiagnosticsCollectionEvent event) {
        super(event.getSelector(), event.getResourceId(), event.getAccountId(), event.getEnvironmentCrn(), event.getOperationId());
    }
}
