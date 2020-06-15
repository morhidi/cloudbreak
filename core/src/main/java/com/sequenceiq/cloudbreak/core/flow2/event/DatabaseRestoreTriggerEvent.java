package com.sequenceiq.cloudbreak.core.flow2.event;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import reactor.rx.Promise;

public class DatabaseRestoreTriggerEvent extends StackEvent {

    private final String backupLocation;

    private final String backupId;

    public DatabaseRestoreTriggerEvent(String selector, Long stackId, String backupLocation, String backupId) {
        super(selector, stackId);
        this.backupLocation = backupLocation;
        this.backupId = backupId;
    }

    public DatabaseRestoreTriggerEvent(String event, Long resourceId, Promise<AcceptResult> accepted,
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
