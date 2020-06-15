package com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

import static com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.backup.DatabaseBackupEvent.DATABASE_BACKUP_FAILED_EVENT;

public class DatabaseBackupFailedEvent extends StackEvent {

    private Exception exception;

    private DetailedStackStatus detailedStatus;

    public DatabaseBackupFailedEvent(Long stackId, Exception exception, DetailedStackStatus detailedStatus) {
        super(stackId);
        this.exception = exception;
        this.detailedStatus = detailedStatus;
    }

    public static DatabaseBackupFailedEvent from(StackEvent event, Exception exception, DetailedStackStatus detailedStatus) {
        return new DatabaseBackupFailedEvent(event.getResourceId(), exception, detailedStatus);
    }

    @Override
    public String selector() {
        return DATABASE_BACKUP_FAILED_EVENT.event();
    }

    public Exception getException() {
        return exception;
    }

    public DetailedStackStatus getDetailedStatus() {
        return detailedStatus;
    }
}
