package com.sequenceiq.cloudbreak.audit.model;

public enum AuditEventName {
    CREATE_DATAHUB_CLUSTER,
    DELETE_DATAHUB_CLUSTER,
    STOP_DATAHUB_CLUSTER,
    RESIZE_DATAHUB_CLUSTER,
    MANUAL_REPAIR_DATAHUB_CLUSTER,
    SYNC_DATAHUB_CLUSTER,
    RETRY_DATAHUB_CLUSTER,
    INSTANCE_DELETE_DATAHUB_CLUSTER,
    MAINTAIN_DATAHUB_CLUSTER,
    START_DATAHUB_CLUSTER,

    CREATE_DATALAKE_CLUSTER,
    DELETE_DATALAKE_CLUSTER,
    STOP_DATALAKE_CLUSTER,
    RESIZE_DATALAKE_CLUSTER,
    MANUAL_REPAIR_DATALAKE_CLUSTER,
    SYNC_DATALAKE_CLUSTER,
    RETRY_DATALAKE_CLUSTER,
    INSTANCE_DELETE_DATALAKE_CLUSTER,
    MAINTAIN_DATALAKE_CLUSTER,
    START_DATALAKE_CLUSTER
}
