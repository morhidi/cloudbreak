package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import com.sequenceiq.freeipa.entity.Stack;

public class CollectionStartRequest extends AbstractLogCollectionEvent  {

    public CollectionStartRequest(LogCollectionEvent event) {
        super(event);
    }

    public CollectionStartRequest(String selector, Long stackId, String accountId, String environmentCrn) {
        super(selector, stackId, accountId, environmentCrn);
    }
}
