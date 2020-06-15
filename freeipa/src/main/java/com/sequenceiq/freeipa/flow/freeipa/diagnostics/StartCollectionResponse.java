package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

public class StartCollectionResponse extends AbstractLogCollectionEvent {
    public StartCollectionResponse(String selector, Long stackId, String accountId, String environmentCrn) {
        super(selector, stackId, accountId, environmentCrn);
    }

    public StartCollectionResponse(LogCollectionEvent event) {
        super(event);
    }
}
