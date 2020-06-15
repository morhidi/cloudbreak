package com.sequenceiq.datalake.flow.dr.event;

import com.sequenceiq.datalake.entity.SdxDatabaseDrStatus;

public class DatalakeDatabaseRestoreStartEvent extends DatalakeDatabaseDrStartBaseEvent {
    private String databaseHost;

    private String backupId;

    private String backupLocation;

    public DatalakeDatabaseRestoreStartEvent(String selector, Long sdxId, String userId,
            String databaseHost, String backupId, String backupLocation) {
        super(selector, sdxId, userId, SdxDatabaseDrStatus.SdxDatabaseDrStatusTypeEnum.RESTORE);
        this.databaseHost = databaseHost;
        this.backupId = backupId;
        this.backupLocation = backupLocation;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public String getBackupId() {
        return backupId;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    @Override
    public String selector() {
        return "DatalakeDatabaseRestoreStartEvent";
    }
}
