package com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

import static com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.restore.DatabaseRestoreEvent.DATABASE_RESTORE_FAILED_EVENT;

public class DatabaseRestoreFailedEvent extends StackEvent {

    private Exception exception;

    private DetailedStackStatus detailedStatus;

    public DatabaseRestoreFailedEvent(Long stackId, Exception exception, DetailedStackStatus detailedStatus) {
        super(stackId);
        this.exception = exception;
        this.detailedStatus = detailedStatus;
    }

    public static DatabaseRestoreFailedEvent from(StackEvent event, Exception exception, DetailedStackStatus detailedStatus) {
        return new DatabaseRestoreFailedEvent(event.getResourceId(), exception, detailedStatus);
    }

    @Override
    public String selector() {
        return DATABASE_RESTORE_FAILED_EVENT.event();
    }

    public Exception getException() {
        return exception;
    }

    public DetailedStackStatus getDetailedStatus() {
        return detailedStatus;
    }
}
