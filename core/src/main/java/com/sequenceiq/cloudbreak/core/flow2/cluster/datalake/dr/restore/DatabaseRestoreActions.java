package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.restore;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.BackupRestoreContext;
import com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.BackupRestoreStatusService;
import com.sequenceiq.cloudbreak.core.flow2.event.DatabaseRestoreTriggerEvent;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore.DatabaseRestoreFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore.DatabaseRestoreRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore.DatabaseRestoreSuccess;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.flow.core.Flow;
import com.sequenceiq.flow.core.FlowEvent;
import com.sequenceiq.flow.core.FlowParameters;
import com.sequenceiq.flow.core.FlowState;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import static com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.restore.DatabaseRestoreEvent.DATABASE_RESTORE_FAILED_EVENT;

@Configuration
public class DatabaseRestoreActions {

    @Inject
    private BackupRestoreStatusService backupRestoreStatusService;

    @Inject
    private StackService stackService;

    @Bean(name = "DATABASE_RESTORE_STATE")
    public Action<?, ?> restoreDatabase() {
        return new AbstractDatabaseRestoreAction<>(DatabaseRestoreTriggerEvent.class) {

            @Override
            protected BackupRestoreContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
                    DatabaseRestoreTriggerEvent payload) {
                return BackupRestoreContext.from(flowParameters, payload, payload.getBackupLocation(), payload.getBackupId());
            }

            @Override
            protected void doExecute(BackupRestoreContext context, DatabaseRestoreTriggerEvent payload, Map<Object, Object> variables) {
                backupRestoreStatusService.restoreDatabase(context.getStackId(), context.getBackupId());
                sendEvent(context);
            }

            @Override
            protected Selectable createRequest(BackupRestoreContext context) {
                return new DatabaseRestoreRequest(context.getStackId(), context.getBackupLocation(), context.getBackupId());
            }

            @Override
            protected Object getFailurePayload(DatabaseRestoreTriggerEvent payload, Optional<BackupRestoreContext> flowContext, Exception ex) {
                return DatabaseRestoreFailedEvent.from(payload, ex, DetailedStackStatus.DATABASE_RESTORE_FAILED);
            }
        };
    }

    @Bean(name = "DATABASE_RESTORE_FINISHED_STATE")
    public Action<?, ?> databaseRestoreFinished() {
        return new AbstractDatabaseRestoreAction<>(DatabaseRestoreSuccess.class) {

            @Override
            protected BackupRestoreContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
                    DatabaseRestoreSuccess payload) {
                return BackupRestoreContext.from(flowParameters, payload, null, null);
            }

            @Override
            protected void doExecute(BackupRestoreContext context, DatabaseRestoreSuccess payload, Map<Object, Object> variables) {
                backupRestoreStatusService.restoreDatabaseFinished(context.getStackId());
                sendEvent(context);
            }

            @Override
            protected Selectable createRequest(BackupRestoreContext context) {
                return new StackEvent(DatabaseRestoreEvent.DATABASE_RESTORE_FINALIZED_EVENT.event(), context.getStackId());
            }

            @Override
            protected Object getFailurePayload(DatabaseRestoreSuccess payload, Optional<BackupRestoreContext> flowContext, Exception ex) {
                return null;
            }
        };
    }

    @Bean(name = "DATABASE_RESTORE_FAILED_STATE")
    public Action<?, ?> databaseRestoreFailedAction() {
        return new AbstractDatabaseRestoreAction<>(DatabaseRestoreFailedEvent.class) {

            @Override
            protected BackupRestoreContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
                    DatabaseRestoreFailedEvent payload) {
                Flow flow = getFlow(flowParameters.getFlowId());
                Stack stack = stackService.getById(payload.getResourceId());
                MDCBuilder.buildMdcContext(stack);
                flow.setFlowFailed(payload.getException());
                return BackupRestoreContext.from(flowParameters, payload, null, null);
            }

            @Override
            protected void doExecute(BackupRestoreContext context, DatabaseRestoreFailedEvent payload, Map<Object, Object> variables) throws Exception {
                backupRestoreStatusService.handleDatabaseRestoreFailure(context.getStackId(), payload.getException().getMessage(), payload.getDetailedStatus());
                sendEvent(context, DATABASE_RESTORE_FAILED_EVENT.event(), payload);
            }

            @Override
            protected Object getFailurePayload(DatabaseRestoreFailedEvent payload, Optional<BackupRestoreContext> flowContext, Exception ex) {
                return null;
            }
        };
    }
}
