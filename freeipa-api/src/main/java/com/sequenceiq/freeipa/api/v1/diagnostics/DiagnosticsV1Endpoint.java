package com.sequenceiq.freeipa.api.v1.diagnostics;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.jerseyclient.RetryAndMetrics;
import com.sequenceiq.freeipa.api.v1.diagnostics.docs.DiagnosticsOperationDescriptions;
import com.sequenceiq.freeipa.api.v1.diagnostics.model.LogCollectionRequest;
import com.sequenceiq.freeipa.api.v1.diagnostics.model.LogCollectionResponse;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.doc.FreeIpaNotes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/v1/diagnostics")
@RetryAndMetrics
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/v1/diagnostics", description = "Diagnostics in FreeIPA", protocols = "http,https", consumes = MediaType.APPLICATION_JSON)
public interface DiagnosticsV1Endpoint {

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = DiagnosticsOperationDescriptions.COLLECT_FREEIPA_LOGS, produces = MediaType.APPLICATION_JSON, notes = FreeIpaNotes.FREEIPA_NOTES,
    nickname = "collectFreeIpaLogsV1")
    LogCollectionResponse collectLogs(@Valid LogCollectionRequest request);
}
