package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import java.util.Map;
import java.util.Set;

public class DiagnosticsCollectionFailureEvent extends AbstractDiagnosticsCollectionEvent {

    private final String failedPhase;

    private final Map<String, String> failureDetails;

    private final Set<String> success;

    public DiagnosticsCollectionFailureEvent(DiagnosticsCollectionEvent event, String failedPhase, Map<String, String> failureDetails,
            Set<String> success) {
        super(event);
        this.failedPhase = failedPhase;
        this.failureDetails = failureDetails;
        this.success = success;
    }

    public String getFailedPhase() {
        return failedPhase;
    }

    public Map<String, String> getFailureDetails() {
        return failureDetails;
    }

    public Set<String> getSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "DiagnosticsCollectionFailureEvent{" +
                "failedPhase='" + failedPhase + '\'' +
                ", failureDetails=" + failureDetails +
                ", success=" + success +
                '}';
    }
}
