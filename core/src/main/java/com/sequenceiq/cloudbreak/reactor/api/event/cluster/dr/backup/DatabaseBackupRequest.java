package com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup;

import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

public class DatabaseBackupRequest extends StackEvent {

    private String backupLocation;

    private String backupId;

    public DatabaseBackupRequest(Long stackId, String backupLocation, String backupId) {
        super(stackId);
        this.backupLocation = backupLocation;
        this.backupId = backupId;
    }

    @Override
    public String selector() {
        return "DatabaseBackupRequest";
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public String getBackupId() {
        return backupId;
    }
}
