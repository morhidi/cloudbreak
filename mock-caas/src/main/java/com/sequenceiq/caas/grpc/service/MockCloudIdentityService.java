package com.sequenceiq.caas.grpc.service;

import com.cloudera.thunderhead.service.usermanagement.UserManagementProto.AzureCloudIdentityDomain;
import com.cloudera.thunderhead.service.usermanagement.UserManagementProto.AzureCloudIdentityName;
import com.cloudera.thunderhead.service.usermanagement.UserManagementProto.CloudIdentity;
import com.cloudera.thunderhead.service.usermanagement.UserManagementProto.CloudIdentityName;
import com.cloudera.thunderhead.service.usermanagement.UserManagementProto.CloudIdentityDomain;
import org.springframework.stereotype.Service;

@Service
class MockCloudIdentityService {

    CloudIdentity createMockAzureCloudIdentity(String baseName) {
        return CloudIdentity.newBuilder()
                .setCloudIdentityDomain(mockAzureCloudIdentityDomain())
                .setCloudIdentityName(mockAzureCloudIdentityName(baseName))
                .build();
    }

    private CloudIdentityDomain mockAzureCloudIdentityDomain() {
        return CloudIdentityDomain.newBuilder()
                .setAzureCloudIdentityDomain(AzureCloudIdentityDomain.newBuilder()
                        .setAzureAdIdentifier("mock-azure-id-domain")
                        .build())
                .build();
    }

    private CloudIdentityName mockAzureCloudIdentityName(String baseName) {
        String mockAzureObjectId = "mock-azure-object-id-" + baseName;
        return CloudIdentityName.newBuilder()
                .setAzureCloudIdentityName(AzureCloudIdentityName.newBuilder()
                        .setObjectId(mockAzureObjectId)
                        .build())
                .build();
    }
}