package com.sequenceiq.cloudbreak.audit.config;


import com.sequenceiq.cloudbreak.audit.converter.AttemptAuditEventResultBuilderUpdater;
import com.sequenceiq.cloudbreak.audit.converter.AuditEventBuilderUpdater;
import io.netty.util.internal.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sequenceiq.cloudbreak.util.NullUtil.getIfNotNull;

@Configuration
public class AuditConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditConfig.class);

    @Value("${altus.audit.host:}")
    private String endpoint;

    @Value("${altus.audit.port:8989}")
    private int port;

    @Inject
    private List<AuditEventBuilderUpdater> auditEventBuilderUpdaters;

    @Inject
    private List<AttemptAuditEventResultBuilderUpdater> attemptAuditEventResultBuilderUpdaters;

    public String getEndpoint() {
        return endpoint;
    }

    public int getPort() {
        return port;
    }

    public boolean isConfigured() {
        return !StringUtil.isNullOrEmpty(endpoint);
    }

    @Bean
    public Map<Class, AuditEventBuilderUpdater> eventDataUpdaters() {
        Map<Class, AuditEventBuilderUpdater> result = new LinkedHashMap<>(auditEventBuilderUpdaters.size());
        auditEventBuilderUpdaters.forEach(updater -> result.put(updater.getType(), updater));
        if (MapUtils.isNotEmpty(result)) {
            String eventDataUpdaters = result.entrySet().stream().map(auditEventDataUpdaterEntry -> String.format("[%s :: %s]",
                    getIfNotNull(auditEventDataUpdaterEntry.getKey(), Class::getSimpleName),
                    getIfNotNull(auditEventDataUpdaterEntry.getValue(), u -> u.getClass().getSimpleName()))).collect(Collectors.joining(","));
            LOGGER.debug("The " + AuditEventBuilderUpdater.class.getSimpleName() + " has the following implementations: {}", eventDataUpdaters);
        } else {
            LOGGER.debug("The " + AuditEventBuilderUpdater.class.getSimpleName() + " has no any implementation!");
        }
        return result;
    }

    @Bean
    public Map<Class, AttemptAuditEventResultBuilderUpdater> auditEventDataUpdaters() {
        Map<Class, AttemptAuditEventResultBuilderUpdater> result = new LinkedHashMap<>(attemptAuditEventResultBuilderUpdaters.size());
        attemptAuditEventResultBuilderUpdaters.forEach(updater -> result.put(updater.getType(), updater));
        if (MapUtils.isNotEmpty(result)) {
            String auditEventDataUpdaters = result.entrySet().stream().map(auditEventDataUpdaterEntry -> String.format("[%s :: %s]",
                    getIfNotNull(auditEventDataUpdaterEntry.getKey(), Class::getSimpleName),
                    getIfNotNull(auditEventDataUpdaterEntry.getValue(), u -> u.getClass().getSimpleName()))).collect(Collectors.joining(","));
            LOGGER.debug("The " + AttemptAuditEventResultBuilderUpdater.class.getSimpleName() + " has the following implementations: {}",
                    auditEventDataUpdaters);
        } else {
            LOGGER.debug("The " + AttemptAuditEventResultBuilderUpdater.class.getSimpleName() + " has no any implementation!");
        }
        return result;
    }

}
