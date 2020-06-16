package com.sequenceiq.freeipa.service.diagnostics;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.freeipa.api.v1.diagnostics.model.DiagnosticsCollectionRequest;
import com.sequenceiq.freeipa.api.v1.diagnostics.model.DiagnosticsCollectionResponse;
import com.sequenceiq.freeipa.api.v1.operation.model.OperationStatus;
import com.sequenceiq.freeipa.api.v1.operation.model.OperationType;
import com.sequenceiq.freeipa.converter.operation.OperationToOperationStatusConverter;
import com.sequenceiq.freeipa.entity.Operation;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.flow.freeipa.cleanup.FreeIpaCleanupEvent;
import com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionEvent;
import com.sequenceiq.freeipa.flow.freeipa.diagnostics.DiagnosticsCollectionEvent;
import com.sequenceiq.freeipa.service.freeipa.flow.FreeIpaFlowManager;
import com.sequenceiq.freeipa.service.operation.OperationService;
import com.sequenceiq.freeipa.service.stack.StackService;

@Service
public class DiagnosticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticsService.class);

    @Inject
    private StackService stackService;

    @Inject
    private OperationService operationService;

    @Inject
    private FreeIpaFlowManager flowManager;

    @Inject
    private OperationToOperationStatusConverter operationToOperationStatusConverter;

    public OperationStatus collect(DiagnosticsCollectionRequest request, String accountId) {
        String environmentCrn = request.getEnvironmentCrn();
        Stack stack = stackService.getByEnvironmentCrnAndAccountIdWithLists(environmentCrn, accountId);
        MDCBuilder.buildMdcContext(stack);
        Operation operation = operationService.startOperation(accountId, OperationType.DIAGNOSTICS_COLLECTION, Set.of(environmentCrn), Set.of());
        DiagnosticsCollectionEvent cleanupEvent = new DiagnosticsCollectionEvent(FreeIpaDiagnosticsCollectionEvent.COLLECTION_EVENT.event(), stack.getId(),
                accountId, environmentCrn, operation.getOperationId());
        flowManager.notify(FreeIpaDiagnosticsCollectionEvent.COLLECTION_EVENT.event(), cleanupEvent);
        return operationToOperationStatusConverter.convert(operation);
    }
}
