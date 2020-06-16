package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import com.sequenceiq.flow.core.FlowEvent;
import com.sequenceiq.flow.event.EventSelectorUtil;

public enum FreeIpaDiagnosticsCollectionEvent implements FlowEvent {

    COLLECTION_EVENT("COLLECT_EVENT"),
    COLLECTION_STARTED_FINISHED_EVENT(EventSelectorUtil.selector(StartCollectionResponse.class)),
    COLLECTION_FINISHED_EVENT("COLLECTION_FINISHED_EVENT"),
    COLLECTION_FAILED_EVENT("COLLECTION_FAILED_EVENT"),
    COLLECTION_FAILURE_HANDLED_EVENT("COLLECTION_FAILURE_HANDLED_EVENT");

    private final String event;

    FreeIpaDiagnosticsCollectionEvent(String event) {
        this.event = event;
    }

    @Override
    public String event() {
        return event;
    }
}
