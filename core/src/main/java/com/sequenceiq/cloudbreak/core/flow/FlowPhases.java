package com.sequenceiq.cloudbreak.core.flow;

public enum FlowPhases {
    PROVISIONING_SETUP,
    PROVISIONING,
    METADATA_SETUP,
    TLS_SETUP,
    BOOTSTRAP_CLUSTER,
    CONSUL_METADATA_SETUP,
    RUN_CLUSTER_CONTAINERS,
    AMBARI_START,
    CLUSTER_INSTALL,
    CLUSTER_RESET,
    ENABLE_KERBEROS,
    STACK_CREATION_FAILED,
    STACK_START,
    STACK_STOP_REQUESTED,
    CLUSTER_START_REQUESTED,
    STACK_STATUS_UPDATE_FAILED,
    STACK_STOP,
    ADD_INSTANCES,
    REMOVE_INSTANCE,
    EXTEND_METADATA,
    BOOTSTRAP_NEW_NODES,
    ADD_CLUSTER_CONTAINERS,
    EXTEND_CONSUL_METADATA,
    STACK_DOWNSCALE,
    CLUSTER_START,
    CLUSTER_STATUS_UPDATE_FAILED,
    CLUSTER_STOP,
    CLUSTER_UPSCALE,
    CLUSTER_DOWNSCALE,
    TERMINATION,
    TERMINATION_FAILED,
    UPDATE_ALLOWED_SUBNETS,
    CLUSTER_SYNC,
    STACK_SYNC,
    NONE;
}
