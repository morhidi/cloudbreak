package com.sequenceiq.cloudbreak.converter.stack.cluster.gateway;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.stack.cluster.ClusterRequest;
import com.sequenceiq.cloudbreak.api.model.stack.cluster.gateway.GatewayJson;
import com.sequenceiq.cloudbreak.controller.exception.BadRequestException;
import com.sequenceiq.cloudbreak.controller.validation.ValidationResult;
import com.sequenceiq.cloudbreak.controller.validation.stack.cluster.gateway.GatewayJsonValidator;
import com.sequenceiq.cloudbreak.converter.AbstractConversionServiceAwareConverter;
import com.sequenceiq.cloudbreak.converter.util.GatewayConvertUtil;
import com.sequenceiq.cloudbreak.domain.stack.cluster.gateway.Gateway;

@Component
public class ClusterRequestToGatewayConverter extends AbstractConversionServiceAwareConverter<ClusterRequest, Gateway> {

    @Inject
    private GatewayConvertUtil gatewayConvertUtil;

    @Inject
    private GatewayJsonValidator gatewayJsonValidator;

    @Override
    public Gateway convert(ClusterRequest cluster) {
        GatewayJson gatewayJson = cluster.getGateway();
        ValidationResult validationResult = gatewayJsonValidator.validate(gatewayJson);
        if (validationResult.hasError()) {
            throw new BadRequestException(validationResult.getFormattedErrors());
        }
        boolean legacyGatewayRequest = gatewayConvertUtil.isLegacyGatewayRequest(gatewayJson);
        if (legacyGatewayRequest && !gatewayJson.isEnableGateway()) {
            return null;
        }
        Gateway gateway = new Gateway();
        gatewayConvertUtil.setBasicProperties(gatewayJson, gateway);
        gatewayConvertUtil.setTopologies(gatewayJson, gateway);
        gatewayConvertUtil.setGatewayPathAndSsoProvider(cluster.getName(), gatewayJson, gateway);
        return gateway;
    }
}
