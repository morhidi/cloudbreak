package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import com.sequenceiq.flow.core.FlowState;

public enum FreeIpaDiagnosticsCollectionState implements FlowState {
    INIT_STATE,
    COLLECTION_STARTED_STATE,
    COLLECTION_FINISHED_STATE,
    COLLECTION_FAILED_STATE,
    FINAL_STATE;
}
