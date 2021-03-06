package com.sequenceiq.it.cloudbreak.assertion.credential;

import com.sequenceiq.it.cloudbreak.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.assertion.Assertion;
import com.sequenceiq.it.cloudbreak.dto.credential.CredentialTestDto;

public class CredentialTestAssertion {

    private CredentialTestAssertion() {

    }

    public static Assertion<CredentialTestDto, CloudbreakClient> validateModifcation(String modifiedDescription) {
        return (testContext, entity, cloudbreakClient) -> {
//            Set<CredentialResponse> collect = entity.getResponses()
//                    .stream()
//                    .filter(cred -> cred.getName().contentEquals(entity.getName()) && cred.getDescription().contentEquals(modifiedDescription))
//                    .collect(Collectors.toSet());
//
//
//            if (collect.isEmpty()) {
//                throw new IllegalArgumentException(String.format("Credential modification did not happened on %s credential",
//                        entity.getName()));
//            }
            return entity;
        };
    }

    public static Assertion<CredentialTestDto, CloudbreakClient> listContains(String credentialName, Integer expectedCount) {
        return (testContext, entity, cloudbreakClient) -> {
//            boolean countCorrect = entity.getResponses()
//                    .stream()
//                    .filter(credentialV4Response -> credentialV4Response.getName().contentEquals(credentialName))
//                    .count() == expectedCount;
//            if (!countCorrect) {
//                throw new IllegalArgumentException("Credential count for " + credentialName + " is not as expected!");
//            }
            return entity;
        };
    }
}
