package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import com.sequenceiq.freeipa.flow.stack.StackEvent;

public class DiagnosticsCollectionEvent extends StackEvent {

    private final String selector;
    private final String accountId;
    private final String environmentCrn;
    private final String operationId;

    public DiagnosticsCollectionEvent(String selector, Long stackId, String accountId, String environmentCrn, String operationId) {
        super(stackId);
        this.selector = selector;
        this.accountId = accountId;
        this.environmentCrn = environmentCrn;
        this.operationId = operationId;
    }

    public String getSelector() {
        return selector;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEnvironmentCrn() {
        return environmentCrn;
    }

    public String getOperationId() {
        return operationId;
    }

    @Override
    public String toString() {
        return "DiagnosticsCollectionEvent{" +
                "selector='" + selector + '\'' +
                ", accountId='" + accountId + '\'' +
                ", environmentCrn='" + environmentCrn + '\'' +
                ", operationId='" + operationId + '\'' +
                '}';
    }
}
