package com.sequenceiq.cloudbreak.core.flow2.event;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import reactor.rx.Promise;

public class DatabaseBackupTriggerEvent extends StackEvent {

    private final String backupLocation;

    private final String backupId;

    public DatabaseBackupTriggerEvent(String selector, Long stackId, String backupLocation, String backupId) {
        super(selector, stackId);
        this.backupLocation = backupLocation;
        this.backupId = backupId;
    }

    public DatabaseBackupTriggerEvent(String event, Long resourceId, Promise<AcceptResult> accepted,
            String backupLocation, String backupId) {
        super(event, resourceId, accepted);
        this.backupLocation = backupLocation;
        this.backupId = backupId;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public String getBackupId() {
        return backupId;
    }
}
