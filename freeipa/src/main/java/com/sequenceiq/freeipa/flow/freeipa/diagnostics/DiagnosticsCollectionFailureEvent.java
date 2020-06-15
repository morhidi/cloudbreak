package com.sequenceiq.freeipa.flow.freeipa.diagnostics;

import java.util.Map;
import java.util.Set;

import com.sequenceiq.freeipa.flow.freeipa.cleanup.CleanupEvent;
import com.sequenceiq.freeipa.flow.freeipa.cleanup.event.AbstractCleanupEvent;

public class LogCollectionFailureEvent extends AbstractLogCollectionEvent {

    private final String failedPhase;

    private final Map<String, String> failureDetails;

    private final Set<String> success;

    public LogCollectionFailureEvent(LogCollectionEvent event, String failedPhase, Map<String, String> failureDetails,
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
        return "LogCollectionFailureEvent{" +
                "failedPhase='" + failedPhase + '\'' +
                ", failureDetails=" + failureDetails +
                ", success=" + success +
                '}';
    }
}
