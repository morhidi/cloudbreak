package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

import com.sequenceiq.freeipa.api.v1.freeipa.user.model.FailureDetails;
import com.sequenceiq.freeipa.api.v1.freeipa.user.model.SuccessDetails;
import com.sequenceiq.freeipa.service.operation.OperationService;

@Configuration
public class FreeIpaDiagnosticsCollectionActions {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeIpaDiagnosticsCollectionActions.class);

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
                        payload.getAccountId(), payload.getEnvironmentCrn(), payload.getOperationId());
                SuccessDetails successDetails = new SuccessDetails(payload.getEnvironmentCrn());
                operationService.completeOperation(payload.getAccountId(), payload.getOperationId(), List.of(successDetails), List.of());
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
                String environtmentCrn = payload.getEnvironmentCrn();
                SuccessDetails successDetails = new SuccessDetails(environtmentCrn);

                successDetails.getAdditionalDetails()
                        .put(payload.getFailedPhase(), Optional.ofNullable(payload.getSuccess()).map(ArrayList::new).orElse(new ArrayList<>()));
                String message = "Diagnostics collection failed during " + payload.getFailedPhase();
                FailureDetails failureDetails = new FailureDetails(environtmentCrn, message);
                if (payload.getFailedPhase() != null) {
                    failureDetails.getAdditionalDetails().putAll(payload.getFailureDetails());
                }
                operationService.failOperation(payload.getAccountId(), payload.getOperationId(), message, List.of(successDetails), List.of(failureDetails));
                sendEvent(context, FreeIpaDiagnosticsCollectionEvent.COLLECTION_FAILURE_HANDLED_EVENT.event(), payload);
            }

        };
    }
}
