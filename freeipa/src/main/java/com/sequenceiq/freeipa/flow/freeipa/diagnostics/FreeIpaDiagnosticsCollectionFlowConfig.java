package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionEvent.COLLECTION_EVENT;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionEvent.COLLECTION_FAILED_EVENT;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionEvent.COLLECTION_FAILURE_HANDLED_EVENT;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionEvent.COLLECTION_FINISHED_EVENT;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionEvent.COLLECTION_STARTED_FINISHED_EVENT;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionState.COLLECTION_FAILED_STATE;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionState.COLLECTION_FINISHED_STATE;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionState.COLLECTION_STARTED_STATE;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionState.FINAL_STATE;
import static com.sequenceiq.freeipa.flow.freeipa.diagnostics.FreeIpaDiagnosticsCollectionState.INIT_STATE;

import java.util.List;

import com.sequenceiq.flow.core.config.AbstractFlowConfiguration;
import com.sequenceiq.flow.core.config.AbstractFlowConfiguration.Transition.Builder;

public class FreeIpaDiagnosticsCollectionFlowConfig extends AbstractFlowConfiguration<FreeIpaDiagnosticsCollectionState, FreeIpaDiagnosticsCollectionEvent> {

    private static final FreeIpaDiagnosticsCollectionEvent[] FREEIPA_INIT_EVENTS = { COLLECTION_EVENT};

    private static final FlowEdgeConfig<FreeIpaDiagnosticsCollectionState, FreeIpaDiagnosticsCollectionEvent> EDGE_CONFIG =
            new FlowEdgeConfig<>(INIT_STATE, FINAL_STATE, COLLECTION_FAILED_STATE, COLLECTION_FAILURE_HANDLED_EVENT);

    private static final List<Transition<FreeIpaDiagnosticsCollectionState, FreeIpaDiagnosticsCollectionEvent>> TRANSITIONS =
            new Builder<FreeIpaDiagnosticsCollectionState, FreeIpaDiagnosticsCollectionEvent>().defaultFailureEvent(COLLECTION_FAILED_EVENT)
            .from(INIT_STATE).to(COLLECTION_STARTED_STATE).event(COLLECTION_EVENT).defaultFailureEvent()
            .from(COLLECTION_STARTED_STATE).to(COLLECTION_FINISHED_STATE).event(COLLECTION_STARTED_FINISHED_EVENT).defaultFailureEvent()
            .from(COLLECTION_FINISHED_STATE).to(FINAL_STATE).event(COLLECTION_FINISHED_EVENT).defaultFailureEvent()
            .build();

    public FreeIpaDiagnosticsCollectionFlowConfig() {
        super(FreeIpaDiagnosticsCollectionState.class, FreeIpaDiagnosticsCollectionEvent.class);
    }

    @Override
    protected List<Transition<FreeIpaDiagnosticsCollectionState, FreeIpaDiagnosticsCollectionEvent>> getTransitions() {
        return TRANSITIONS;
    }

    @Override
    protected FlowEdgeConfig<FreeIpaDiagnosticsCollectionState, FreeIpaDiagnosticsCollectionEvent> getEdgeConfig() {
        return EDGE_CONFIG;
    }

    @Override
    public FreeIpaDiagnosticsCollectionEvent[] getEvents() {
        return FreeIpaDiagnosticsCollectionEvent.values();
    }

    @Override
    public FreeIpaDiagnosticsCollectionEvent[] getInitEvents() {
        return FREEIPA_INIT_EVENTS;
    }

    @Override
    public String getDisplayName() {
        return "Collect diagnostical data from FreeIPA";
    }
}
