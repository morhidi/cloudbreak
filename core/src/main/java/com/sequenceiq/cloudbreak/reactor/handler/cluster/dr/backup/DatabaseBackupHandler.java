package com.sequenceiq.cloudbreak.reactor.handler.cluster.dr.backup;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.bootstrap.service.ClusterDeletionBasedExitCriteriaModel;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.cloudbreak.orchestrator.host.HostOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.model.GatewayConfig;
import com.sequenceiq.cloudbreak.orchestrator.model.SaltConfig;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteriaModel;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup.DatabaseBackupFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup.DatabaseBackupRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.backup.DatabaseBackupSuccess;
import com.sequenceiq.cloudbreak.reactor.handler.cluster.dr.HandlerMethods;
import com.sequenceiq.cloudbreak.service.GatewayConfigService;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.cloudbreak.util.StackUtil;
import com.sequenceiq.flow.reactor.api.handler.ExceptionCatcherEventHandler;
import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DatabaseBackupHandler extends ExceptionCatcherEventHandler<DatabaseBackupRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBackupHandler.class);

    @Inject
    private GatewayConfigService gatewayConfigService;

    @Inject
    private HostOrchestrator hostOrchestrator;

    @Inject
    private StackService stackService;

    @Inject
    private StackUtil stackUtil;

    @Override
    public String selector() {
        return "DatabaseBackupRequest";
    }

    @Override
    protected Selectable defaultFailureEvent(Long resourceId, Exception e) {
        return new DatabaseBackupFailedEvent(resourceId, e, DetailedStackStatus.DATABASE_BACKUP_FAILED);
    }

    @Override
    protected void doAccept(HandlerEvent event) {
        LOGGER.debug("Accepting Database backup event...");
        DatabaseBackupRequest request = event.getData();
        Selectable result;
        Long stackId = request.getResourceId();
        try {
            Stack stack = stackService.getByIdWithListsInTransaction(stackId);
            Cluster cluster = stack.getCluster();
            InstanceMetaData gatewayInstance = stack.getPrimaryGatewayInstance();
            GatewayConfig gatewayConfig = gatewayConfigService.getGatewayConfig(stack, gatewayInstance, cluster.getGateway() != null);
            Set<String> gatewayFQDN = Collections.singleton(gatewayInstance.getDiscoveryFQDN());
            ExitCriteriaModel noExitModel = ClusterDeletionBasedExitCriteriaModel.nonCancellableModel();
            SaltConfig saltConfig = HandlerMethods.createSaltConfig(request.getBackupLocation(), request.getBackupId(), stack.getCloudPlatform());
            hostOrchestrator.backupDatabase(gatewayConfig, gatewayFQDN, stackUtil.collectReachableNodes(stack), saltConfig, noExitModel);

            result = new DatabaseBackupSuccess(stackId);
        } catch (Exception e) {
            LOGGER.info("Database backup event failed", e);
            result = new DatabaseBackupFailedEvent(stackId, e, DetailedStackStatus.DATABASE_BACKUP_FAILED);
        }
        sendEvent(result, event);
    }
}
