package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.restore;

import com.sequenceiq.flow.core.FlowEvent;

public enum DatabaseRestoreEvent implements FlowEvent {
    DATABASE_RESTORE_EVENT("DATABASE_RESTORE_EVENT"),
    DATABASE_RESTORE_FINISHED_EVENT("DATABASE_RESTORE_SUCCESS_EVENT"),
    DATABASE_RESTORE_FAILED_EVENT("DATABASE_RESTORE_FAILED_EVENT"),
    DATABASE_RESTORE_FINALIZED_EVENT("DATABASE_RESTORE_FINALIZED_EVENT"),
    DATABASE_RESTORE_FAIL_HANDLED_EVENT("DATABASE_RESTORE_FAIL_HANDLED_EVENT");

    private final String event;

    DatabaseRestoreEvent(String event) {
        this.event = event;
    }

    @Override
    public String event() {
        return event;
    }
}