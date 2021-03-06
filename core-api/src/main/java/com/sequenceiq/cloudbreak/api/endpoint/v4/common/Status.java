package com.sequenceiq.cloudbreak.api.endpoint.v4.common;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;
import com.sequenceiq.cloudbreak.api.model.StatusKind;

public enum Status {
    REQUESTED(StatusKind.PROGRESS),
    CREATE_IN_PROGRESS(StatusKind.PROGRESS),
    AVAILABLE(StatusKind.FINAL),
    UPDATE_IN_PROGRESS(StatusKind.PROGRESS),
    UPDATE_REQUESTED(StatusKind.PROGRESS),
    BACKUP_IN_PROGRESS(StatusKind.PROGRESS),
    RESTORE_IN_PROGRESS(StatusKind.PROGRESS),
    UPDATE_FAILED(StatusKind.FINAL),
    BACKUP_FAILED(StatusKind.FINAL),
    RESTORE_FAILED(StatusKind.FINAL),
    CREATE_FAILED(StatusKind.FINAL),
    ENABLE_SECURITY_FAILED(StatusKind.FINAL),
    PRE_DELETE_IN_PROGRESS(StatusKind.PROGRESS),
    DELETE_IN_PROGRESS(StatusKind.PROGRESS),
    DELETE_FAILED(StatusKind.FINAL),
    DELETED_ON_PROVIDER_SIDE(StatusKind.FINAL),
    DELETE_COMPLETED(StatusKind.FINAL),
    STOPPED(StatusKind.FINAL),
    STOP_REQUESTED(StatusKind.PROGRESS),
    START_REQUESTED(StatusKind.PROGRESS),
    STOP_IN_PROGRESS(StatusKind.PROGRESS),
    START_IN_PROGRESS(StatusKind.PROGRESS),
    START_FAILED(StatusKind.FINAL),
    STOP_FAILED(StatusKind.FINAL),
    WAIT_FOR_SYNC(StatusKind.PROGRESS),
    MAINTENANCE_MODE_ENABLED(StatusKind.FINAL),
    AMBIGUOUS(StatusKind.FINAL),
    EXTERNAL_DATABASE_CREATION_IN_PROGRESS(StatusKind.PROGRESS),
    EXTERNAL_DATABASE_CREATION_FAILED(StatusKind.FINAL),
    EXTERNAL_DATABASE_DELETION_IN_PROGRESS(StatusKind.PROGRESS),
    EXTERNAL_DATABASE_DELETION_FINISHED(StatusKind.PROGRESS),
    EXTERNAL_DATABASE_DELETION_FAILED(StatusKind.FINAL);

    private StatusKind statusKind;

    Status(StatusKind statusKind) {
        this.statusKind = statusKind;
    }

    public StatusKind getStatusKind() {
        return statusKind;
    }

    public boolean isRemovableStatus() {
        return Arrays.asList(AVAILABLE, UPDATE_FAILED, CREATE_FAILED, ENABLE_SECURITY_FAILED, DELETE_FAILED,
                DELETE_COMPLETED, DELETED_ON_PROVIDER_SIDE, STOPPED, START_FAILED, STOP_FAILED).contains(valueOf(name()));
    }

    public boolean isAvailable() {
        return Arrays.asList(AVAILABLE, MAINTENANCE_MODE_ENABLED).contains(valueOf(name()));
    }

    public boolean isInProgress() {
        return getStatusKind().equals(StatusKind.PROGRESS);
    }

    public boolean isStopped() {
        return STOPPED == this;
    }

    public boolean isStartState() {
        return Status.AVAILABLE.equals(this)
                || UPDATE_IN_PROGRESS.equals(this)
                || Status.START_FAILED.equals(this)
                || Status.START_REQUESTED.equals(this)
                || Status.START_IN_PROGRESS.equals(this);
    }

    public boolean isStopState() {
        return Status.STOPPED.equals(this)
                || UPDATE_IN_PROGRESS.equals(this)
                || Status.STOP_IN_PROGRESS.equals(this)
                || Status.STOP_REQUESTED.equals(this)
                || Status.STOP_FAILED.equals(this);
    }

    public Status mapToFailedIfInProgress() {
        switch (this) {
            case REQUESTED:
            case CREATE_IN_PROGRESS:
                return CREATE_FAILED;
            case UPDATE_IN_PROGRESS:
                return UPDATE_FAILED;
            case DELETE_IN_PROGRESS:
            case PRE_DELETE_IN_PROGRESS:
                return DELETE_FAILED;
            case START_IN_PROGRESS:
                return START_FAILED;
            case STOP_IN_PROGRESS:
                return STOP_FAILED;
            default:
                return this;
        }
    }

    public static Set<Status> getAllowedDataHubStatesForSdxUpgrade() {
        return Sets.immutableEnumSet(STOPPED, DELETE_COMPLETED,
                CREATE_FAILED, DELETE_FAILED, DELETED_ON_PROVIDER_SIDE);
    }
}
