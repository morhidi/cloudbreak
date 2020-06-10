package com.sequenceiq.cloudbreak.audit.converter.builder;

import com.cloudera.thunderhead.service.audit.AuditProto;
import com.sequenceiq.cloudbreak.audit.model.AttemptAuditEventResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.sequenceiq.cloudbreak.util.ConditionBasedEvaluatorUtil.doIfTrue;

@Component
public class AttemptAuditEventResultBuilderProvider {

    public AuditProto.AttemptAuditEventResult.Builder prepareBuilderForCreateAuditEvent(AttemptAuditEventResult source) {
        AuditProto.AttemptAuditEventResult.Builder builder = AuditProto.AttemptAuditEventResult.newBuilder()
                .setId(source.getId())
                .setResultCode(source.getResultCode());
        doIfTrue(source.getResultMessage(), StringUtils::isNotEmpty, builder::setResultMessage);
        return builder;
    }

}
