package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

public class AbstractLogCollectionEvent extends LogCollectionEvent {

    public AbstractLogCollectionEvent(String selector, Long stackId, String accountId, String environmentCrn) {
        super(selector, stackId, accountId, environmentCrn);
    }

    public AbstractLogCollectionEvent(LogCollectionEvent event) {
        super(event.getSelector(), event.getResourceId(), event.getAccountId(), event.getEnvironmentCrn());
    }
}
