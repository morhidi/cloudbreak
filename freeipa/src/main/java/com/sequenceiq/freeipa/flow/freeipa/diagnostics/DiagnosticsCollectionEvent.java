package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import com.sequenceiq.freeipa.flow.stack.StackEvent;

public class LogCollectionEvent extends StackEvent {

    private final String selector;
    private final String accountId;
    private final String environmentCrn;

    public LogCollectionEvent(String selector, Long stackId, String accountId, String environmentCrn) {
        super(stackId);
        this.selector = selector;
        this.accountId = accountId;
        this.environmentCrn = environmentCrn;
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
}
