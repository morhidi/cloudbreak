package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.backup;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.BackupRestoreContext;
import com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.BackupRestoreStatusService;
import com.sequenceiq.cloudbreak.core.flow2.event.DatabaseBackupTriggerEvent;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup.DatabaseBackupFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup.DatabaseBackupRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup.DatabaseBackupSuccess;
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

import static com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.dr.backup.DatabaseBackupEvent.DATABASE_BACKUP_FAILED_EVENT;

@Configuration
public class DatabaseBackupActions {

    @Inject
    private BackupRestoreStatusService backupRestoreStatusService;

    @Inject
    private StackService stackService;

    @Bean(name = "DATABASE_BACKUP_STATE")
    public Action<?, ?> backupDatabase() {
        return new AbstractDatabaseBackupAction<>(DatabaseBackupTriggerEvent.class) {

            @Override
            protected BackupRestoreContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
                    DatabaseBackupTriggerEvent payload) {
                return BackupRestoreContext.from(flowParameters, payload, payload.getBackupLocation(), payload.getBackupId());
            }

            @Override
            protected void doExecute(BackupRestoreContext context, DatabaseBackupTriggerEvent payload, Map<Object, Object> variables) {
                backupRestoreStatusService.backupDatabase(context.getStackId(), context.getBackupId());
                sendEvent(context);
            }

            @Override
            protected Selectable createRequest(BackupRestoreContext context) {
                return new DatabaseBackupRequest(context.getStackId(), context.getBackupLocation(), context.getBackupId());
            }

            @Override
            protected Object getFailurePayload(DatabaseBackupTriggerEvent payload, Optional<BackupRestoreContext> flowContext, Exception ex) {
                return DatabaseBackupFailedEvent.from(payload, ex, DetailedStackStatus.DATABASE_BACKUP_FAILED);
            }
        };
    }

    @Bean(name = "DATABASE_BACKUP_FINISHED_STATE")
    public Action<?, ?> databaseBackupFinished() {
        return new AbstractDatabaseBackupAction<>(DatabaseBackupSuccess.class) {

            @Override
            protected BackupRestoreContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
                    DatabaseBackupSuccess payload) {
                return BackupRestoreContext.from(flowParameters, payload, null, null);
            }

            @Override
            protected void doExecute(BackupRestoreContext context, DatabaseBackupSuccess payload, Map<Object, Object> variables) {
                backupRestoreStatusService.backupDatabaseFinished(context.getStackId());
                sendEvent(context);
            }

            @Override
            protected Selectable createRequest(BackupRestoreContext context) {
                return new StackEvent(DatabaseBackupEvent.DATABASE_BACKUP_FINALIZED_EVENT.event(), context.getStackId());
            }

            @Override
            protected Object getFailurePayload(DatabaseBackupSuccess payload, Optional<BackupRestoreContext> flowContext, Exception ex) {
                return null;
            }
        };
    }

    @Bean(name = "DATABASE_BACKUP_FAILED_STATE")
    public Action<?, ?> databaseBackupFailedAction() {
        return new AbstractDatabaseBackupAction<>(DatabaseBackupFailedEvent.class) {

            @Override
            protected BackupRestoreContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
                    DatabaseBackupFailedEvent payload) {
                Flow flow = getFlow(flowParameters.getFlowId());
                Stack stack = stackService.getById(payload.getResourceId());
                MDCBuilder.buildMdcContext(stack);
                flow.setFlowFailed(payload.getException());
                return BackupRestoreContext.from(flowParameters, payload, null, null);
            }

            @Override
            protected void doExecute(BackupRestoreContext context, DatabaseBackupFailedEvent payload, Map<Object, Object> variables) throws Exception {
                backupRestoreStatusService.handleDatabaseBackupFailure(context.getStackId(), payload.getException().getMessage(), payload.getDetailedStatus());
                sendEvent(context, DATABASE_BACKUP_FAILED_EVENT.event(), payload);
            }

            @Override
            protected Object getFailurePayload(DatabaseBackupFailedEvent payload, Optional<BackupRestoreContext> flowContext, Exception ex) {
                return null;
            }
        };
    }
}
