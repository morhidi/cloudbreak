package com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore;

import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

import static com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.restore.DatabaseRestoreEvent.DATABASE_RESTORE_FINISHED_EVENT;

public class DatabaseRestoreSuccess extends StackEvent {

    public DatabaseRestoreSuccess(Long stackId) {
        super(stackId);
    }

    @Override
    public String selector() {
        return DATABASE_RESTORE_FINISHED_EVENT.event();
    }
}
