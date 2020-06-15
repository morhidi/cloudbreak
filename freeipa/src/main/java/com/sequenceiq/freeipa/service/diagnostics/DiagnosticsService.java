package com.sequenceiq.freeipa.service.diagnostics;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.freeipa.api.v1.diagnostics.model.LogCollectionRequest;
import com.sequenceiq.freeipa.api.v1.diagnostics.model.LogCollectionResponse;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.flow.freeipa.cleanup.FreeIpaCleanupEvent;
import com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaLogCollectionEvent;
import com.sequenceiq.freeipa.flow.freeipa.diagnostics.LogCollectionEvent;
import com.sequenceiq.freeipa.service.freeipa.flow.FreeIpaFlowManager;
import com.sequenceiq.freeipa.service.stack.StackService;

@Service
public class DiagnosticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticsService.class);

    @Inject
    private StackService stackService;

    @Inject
    private FreeIpaFlowManager flowManager;

    public LogCollectionResponse collectLogs(LogCollectionRequest request, String accountId) {
        Stack stack = stackService.getByEnvironmentCrnAndAccountIdWithLists(request.getEnvironmentCrn(), accountId);
        LogCollectionEvent cleanupEvent = new LogCollectionEvent(FreeIpaLogCollectionEvent.COLLECTION_EVENT.event(), stack.getId(), accountId, request.getEnvironmentCrn());
        flowManager.notify(FreeIpaCleanupEvent.CLEANUP_EVENT.event(), cleanupEvent);
        return null;
    }
}
