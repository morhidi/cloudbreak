package com.sequenceiq.freeipa.flow.freeipa.diagnostics.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorFailedException;
import com.sequenceiq.cloudbreak.orchestrator.host.HostOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.model.GatewayConfig;
import com.sequenceiq.cloudbreak.orchestrator.model.Node;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.flow.reactor.api.handler.EventHandler;
import com.sequenceiq.freeipa.client.FreeIpaClientException;
import com.sequenceiq.freeipa.entity.InstanceMetaData;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.flow.freeipa.cleanup.event.cert.RevokeCertsRequest;
import com.sequenceiq.freeipa.flow.freeipa.diagnostics.CollectionStartRequest;
import com.sequenceiq.freeipa.flow.freeipa.diagnostics.StartCollectionResponse;
import com.sequenceiq.freeipa.orchestrator.StackBasedExitCriteriaModel;
import com.sequenceiq.freeipa.service.GatewayConfigService;
import com.sequenceiq.freeipa.service.stack.StackService;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class CollectionStartHandler implements EventHandler<CollectionStartRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionStartHandler.class);

    @Inject
    private EventBus eventBus;

    @Inject
    private StackService stackService;

    @Inject
    private HostOrchestrator hostOrchestrator;

    @Inject
    private GatewayConfigService gatewayConfigService;

    @Override
    public String selector() {
        return EventSelectorUtil.selector(CollectionStartRequest.class);
    }

    @Override
    public void accept(Event<CollectionStartRequest> event) {
        CollectionStartRequest request = event.getData();
        try {
            Stack stack = stackService.getByIdWithListsInTransaction(request.getResourceId());
            Map<String, String> nodePrivateIPsByFQDN = stack.getAllInstanceMetaDataList().stream()
                    .collect(Collectors.toMap(instanceMetaData -> instanceMetaData.getDiscoveryFQDN(), instanceMeataData -> instanceMeataData.getPrivateIp()));
            Set<InstanceMetaData> instanceMetaDatas = new HashSet<>(stack.getNotDeletedInstanceMetaDataList());
            Set<Node> nodes = instanceMetaDatas.stream()
                    .map(im -> new Node(im.getPrivateIp(), im.getPublicIp(), im.getInstanceId(),
                            im.getInstanceGroup().getTemplate().getInstanceType(), im.getDiscoveryFQDN(), im.getInstanceGroup().getGroupName()))
                    .collect(Collectors.toSet());
            List<GatewayConfig> gatewayConfigs = gatewayConfigService.getGatewayConfigs(stack, instanceMetaDatas);
            LOGGER.debug("Tearing down [{}]. The remaining nodes are [{}].", nodePrivateIPsByFQDN, nodes);
            hostOrchestrator.collectLogs(gatewayConfigs, nodePrivateIPsByFQDN, nodes, new StackBasedExitCriteriaModel(stack.getId()));
            StartCollectionResponse response = new StartCollectionResponse(request);
            eventBus.notify(EventSelectorUtil.selector(StartCollectionResponse.class), new Event<>(event.getHeaders(), response));
        } catch (CloudbreakOrchestratorFailedException e) {
            LOGGER.error("Log collection failed for stack: [{}]", request.getResourceId(), e);
            StartCollectionResponse response = new StartCollectionResponse(request);
            eventBus.notify(EventSelectorUtil.failureSelector(StartCollectionResponse.class),
                    new Event<>(event.getHeaders(), response));
        }
    }
}