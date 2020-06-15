package com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore;

import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

public class DatabaseRestoreRequest extends StackEvent {

    private String backupLocation;

    private String backupId;

    public DatabaseRestoreRequest(Long stackId, String backupLocation, String backupId) {
        super(stackId);
        this.backupLocation = backupLocation;
        this.backupId = backupId;
    }

    @Override
    public String selector() {
        return "DatabaseRestoreRequest";
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public String getBackupId() {
        return backupId;
    }
}
