package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

import com.sequenceiq.freeipa.api.v1.freeipa.user.model.SuccessDetails;
import com.sequenceiq.freeipa.service.operation.OperationService;

@Configuration
public class FreeIpaLogCollectionActions {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeIpaLogCollectionActions.class);

    @Bean(name = "COLLECTION_STARTED_STATE")
    public Action<?, ?> revokeCertsAction() {
        return new AbstractFreeIpaDiagnosticsCollectionAction<>(DiagnosticsCollectionEvent.class) {

            @Override
            protected void doExecute(FreeIpaContext context, DiagnosticsCollectionEvent payload, Map<Object, Object> variables) throws Exception {
                CollectionStartRequest request = new CollectionStartRequest(payload);
                sendEvent(context, request);
            }
        };
    }

    @Bean(name = "COLLECTION_FINISHED_STATE")
    public Action<?, ?> cleanupFinishedAction() {
        return new AbstractFreeIpaDiagnosticsCollectionAction<>(StartCollectionResponse.class) {

            @Inject
            private OperationService operationService;

            @Override
            protected void doExecute(FreeIpaContext context, StartCollectionResponse payload, Map<Object, Object> variables) {
                DiagnosticsCollectionEvent cleanupEvent = new DiagnosticsCollectionEvent(FreeIpaDiagnosticsCollectionEvent.COLLECTION_FINISHED_EVENT.event(), payload.getResourceId(),
                        payload.getAccountId(), payload.getEnvironmentCrn(), operation.getOperationId());
                SuccessDetails successDetails = new SuccessDetails(payload.getEnvironmentCrn());
//                operationService.completeOperation(payload.getAccountId(), payload.getOperationId(), List.of(successDetails), Collections.emptyList());
                LOGGER.info("Cleanup successfully finished with: " + successDetails);
                sendEvent(context, cleanupEvent);
            }
        };
    }

    @Bean(name = "COLLECTION_FAILED_STATE")
    public Action<?, ?> cleanupFailureAction() {
        return new AbstractFreeIpaDiagnosticsCollectionAction<>(DiagnosticsCollectionFailureEvent.class) {

            @Inject
            private OperationService operationService;

            @Override
            protected void doExecute(FreeIpaContext context, DiagnosticsCollectionFailureEvent payload, Map<Object, Object> variables) {

//                operationService.failOperation(payload.getAccountId(), payload.getOperationId(), message, List.of(successDetails), List.of(failureDetails));
                sendEvent(context, FreeIpaDiagnosticsCollectionEvent.COLLECTION_FAILURE_HANDLED_EVENT.event(), payload);
            }

        };
    }
}
