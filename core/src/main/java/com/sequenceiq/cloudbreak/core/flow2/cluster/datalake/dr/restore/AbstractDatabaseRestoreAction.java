package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.restore;

import com.sequenceiq.cloudbreak.common.event.Payload;
import com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.BackupRestoreContext;
import com.sequenceiq.flow.core.AbstractAction;
import com.sequenceiq.flow.core.FlowEvent;
import com.sequenceiq.flow.core.FlowState;

public abstract class AbstractDatabaseRestoreAction<P extends Payload>
    extends AbstractAction<FlowState, FlowEvent, BackupRestoreContext, P> {

    protected AbstractDatabaseRestoreAction(Class<P> payloadClass) {
        super(payloadClass);
    }
}