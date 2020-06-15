package com.sequenceiq.cloudbreak.reactor.handler.cluster.dr.restore;

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
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore.DatabaseRestoreFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore.DatabaseRestoreRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.dr.restore.DatabaseRestoreSuccess;
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
public class DatabaseRestoreHandler extends ExceptionCatcherEventHandler<DatabaseRestoreRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseRestoreHandler.class);

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
        return "DatabaseRestoreRequest";
    }

    @Override
    protected Selectable defaultFailureEvent(Long resourceId, Exception e) {
        return new DatabaseRestoreFailedEvent(resourceId, e, DetailedStackStatus.DATABASE_RESTORE_FAILED);
    }

    @Override
    protected void doAccept(HandlerEvent event) {
        LOGGER.debug("Accepting Database restore event...");
        DatabaseRestoreRequest request = event.getData();
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
            hostOrchestrator.restoreDatabase(gatewayConfig, gatewayFQDN, stackUtil.collectReachableNodes(stack), saltConfig, noExitModel);

            result = new DatabaseRestoreSuccess(stackId);
        } catch (Exception e) {
            LOGGER.info("Database restore event failed", e);
            result = new DatabaseRestoreFailedEvent(stackId, e, DetailedStackStatus.DATABASE_RESTORE_FAILED);
        }
        sendEvent(result, event);
    }
}
