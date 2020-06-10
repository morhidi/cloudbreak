package com.sequenceiq.cloudbreak.audit.converter;

import com.cloudera.thunderhead.service.audit.AuditProto;
import com.sequenceiq.cloudbreak.audit.converter.builder.AttemptAuditEventResultBuilderProvider;
import com.sequenceiq.cloudbreak.audit.model.AttemptAuditEventResult;
import com.sequenceiq.cloudbreak.audit.model.ResultEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AttemptAuditEventResultToGrpcAttemptAuditEventResultConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttemptAuditEventResultToGrpcAttemptAuditEventResultConverter.class);

    private final Map<Class, AttemptAuditEventResultBuilderUpdater> builderUpdater;

    private final AttemptAuditEventResultBuilderProvider builderProvider;

    public AttemptAuditEventResultToGrpcAttemptAuditEventResultConverter(Map<Class, AttemptAuditEventResultBuilderUpdater> builderUpdater,
                    AttemptAuditEventResultBuilderProvider builderProvider) {
        this.builderUpdater = builderUpdater;
        this.builderProvider = builderProvider;
    }

    public AuditProto.AttemptAuditEventResult convert(AttemptAuditEventResult source) {
        AuditProto.AttemptAuditEventResult.Builder attemptAuditEventResultBuilder = builderProvider.prepareBuilderForCreateAuditEvent(source);
        updateResultEventData(attemptAuditEventResultBuilder, source.getResultEventData());
        return attemptAuditEventResultBuilder.build();
    }

    private void updateResultEventData(AuditProto.AttemptAuditEventResult.Builder auditEventBuilder, ResultEventData source) {
        if (source == null) {
            LOGGER.debug("No ResultEventData has provided to update AuditEventData hence no operation will be done.");
            return;
        }
        if (builderUpdater.containsKey(source.getClass())) {
            builderUpdater.get(source.getClass()).update(auditEventBuilder, source);
        } else {
            throw new IllegalArgumentException("ResultEventData has an invalid class: " + source.getClass().getName());
        }
    }

}
