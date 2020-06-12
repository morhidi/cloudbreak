package com.sequenceiq.cloudbreak.cloud.aws.service.subnetselector;

import java.util.Collection;

import com.sequenceiq.cloudbreak.cloud.model.CloudSubnet;
import com.sequenceiq.cloudbreak.cloud.model.SubnetSelectionResult;

public interface SubnetFilterStrategy {
    SubnetSelectionResult filter(Collection<CloudSubnet> subnets, int azCount, boolean internalTenant);

    SubnetFilterStrategyType getType();
}
