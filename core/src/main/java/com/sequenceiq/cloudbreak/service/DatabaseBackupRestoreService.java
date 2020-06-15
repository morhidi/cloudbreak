package com.sequenceiq.cloudbreak.service;

import com.sequenceiq.cloudbreak.core.flow2.service.ReactorFlowManager;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.event.ResourceEvent;
import com.sequenceiq.cloudbreak.exception.BadRequestException;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.cloudbreak.structuredevent.event.CloudbreakEventService;
import com.sequenceiq.flow.api.model.FlowIdentifier;
import com.sequenceiq.flow.core.FlowLogService;
import com.sequenceiq.flow.domain.FlowLog;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import static com.sequenceiq.cloudbreak.exception.NotFoundException.notFoundException;

@Service
public class DatabaseBackupRestoreService {

    private static final String DATALAKE_DATABASE_BACKUP = "DATALAKE_DATABASE_BACKUP";

    private static final String DATALAKE_DATABASE_RESTORE = "DATALAKE_DATABASE_RESTORE";

    @Inject
    private StackService stackService;

    @Inject
    private FlowLogService flowLogService;

    @Inject
    private ReactorFlowManager flowManager;

    @Inject
    private CloudbreakEventService eventService;

    public FlowIdentifier backupDatabase(Long workspaceId, String stackName, String location, String backupId) {
        Optional<Stack> stackOptional = stackService.findStackByNameAndWorkspaceId(stackName, workspaceId);
        if (stackOptional.isPresent()) {
            Stack stack = stackOptional.get();
            List<FlowLog> flowLogs = flowLogService.findAllByResourceIdAndFinalizedIsFalseOrderByCreatedDesc(stack.getId());
            if (!CollectionUtils.isEmpty(flowLogs)) {
                String errorMsg = String.format("Database backup cannot be performed because there is an active flow running: %s",
                    flowLogs.stream().map(FlowLog::toString));
                eventService.fireCloudbreakEvent(
                    stack.getId(),
                    DATALAKE_DATABASE_BACKUP,
                    ResourceEvent.DATALAKE_DATABASE_BACKUP_COULD_NOT_START,
                    List.of(errorMsg));
                throw new BadRequestException(errorMsg);
            } else {
                return flowManager.triggerDatalakeDatabaseBackup(stack.getId(), location, backupId);
            }
        } else {
            throw notFoundException("Stack", stackName);
        }
    }

    public FlowIdentifier restoreDatabase(Long workspaceId, String stackName, String location, String backupId) {
        Optional<Stack> stackOptional = stackService.findStackByNameAndWorkspaceId(stackName, workspaceId);
        if (stackOptional.isPresent()) {
            Stack stack = stackOptional.get();
            List<FlowLog> flowLogs = flowLogService.findAllByResourceIdAndFinalizedIsFalseOrderByCreatedDesc(stack.getId());
            if (!CollectionUtils.isEmpty(flowLogs)) {
                String errorMsg = String.format("Database restore cannot be performed because there is an active flow running: %s",
                    flowLogs.stream().map(FlowLog::toString));
                eventService.fireCloudbreakEvent(
                    stack.getId(),
                    DATALAKE_DATABASE_RESTORE,
                    ResourceEvent.DATALAKE_DATABASE_RESTORE_COULD_NOT_START,
                    List.of(errorMsg));
                throw new BadRequestException(errorMsg);
            } else {
                return flowManager.triggerDatalakeDatabaseRestore(stack.getId(), location, backupId);
            }
        } else {
            throw notFoundException("Stack", stackName);
        }
    }
}
