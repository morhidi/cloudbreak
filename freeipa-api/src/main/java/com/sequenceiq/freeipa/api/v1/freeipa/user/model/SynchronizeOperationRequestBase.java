package com.sequenceiq.freeipa.api.v1.freeipa.user.model;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sequenceiq.authorization.annotation.ResourceObjectField;
import com.sequenceiq.authorization.resource.AuthorizationResourceAction;
import com.sequenceiq.authorization.resource.AuthorizationVariableType;
import com.sequenceiq.freeipa.api.v1.freeipa.user.doc.UserModelDescriptions;

import io.swagger.annotations.ApiModelProperty;

public class SynchronizeOperationRequestBase {
    @NotNull
    @Size(min = 1)
    @ResourceObjectField(variableType = AuthorizationVariableType.CRN_LIST, action = AuthorizationResourceAction.EDIT_ENVIRONMENT)
    @ApiModelProperty(value = UserModelDescriptions.USERSYNC_ENVIRONMENT_CRNS)
    private Set<String> environments = new HashSet<>();

    public SynchronizeOperationRequestBase() {
    }

    public SynchronizeOperationRequestBase(Set<String> environments) {
        this.environments = environments;
    }

    public Set<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(Set<String> environments) {
        this.environments = environments;
    }

    @Override
    public String toString() {
        return "SynchronizeOperationRequestBase{"
                + "environments=" + environments
                + '}';
    }

    protected String fieldsToString() {
        return "environments=" + environments;
    }
}
