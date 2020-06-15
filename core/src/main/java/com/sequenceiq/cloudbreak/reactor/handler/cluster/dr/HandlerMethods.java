package com.sequenceiq.cloudbreak.reactor.handler.cluster.dr;

import com.sequenceiq.cloudbreak.orchestrator.model.SaltConfig;
import com.sequenceiq.cloudbreak.orchestrator.model.SaltPillarProperties;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class HandlerMethods {

    private HandlerMethods() {

    }

    public static SaltConfig createSaltConfig(String location, String backupId, String cloudPlatform) {
        String fullLocation = location + "/" + backupId + "_database_backup";
        String scheme;
        if ("aws".equalsIgnoreCase(cloudPlatform)) {
            scheme = "s3:/";
        } else if ("azure".equalsIgnoreCase(cloudPlatform)) {
            // TODO verify this is right when azure flow is working
            scheme = "abfs:/";
        } else {
            throw new UnsupportedOperationException("Cloud platform " + cloudPlatform + "not supported for backup/restore");
        }

        if (fullLocation.startsWith("/")) {
            fullLocation = scheme + fullLocation;
        } else {
            fullLocation = scheme + fullLocation.substring(fullLocation.indexOf("//") + 1);
        }

        Map<String, SaltPillarProperties> servicePillar = new HashMap<>();
        servicePillar.put("disaster-recovery", new SaltPillarProperties("/postgresql/disaster_recovery.sls",
            singletonMap("disaster_recovery", singletonMap("object_storage_url", fullLocation))));
        return new SaltConfig(servicePillar);
    }
}
