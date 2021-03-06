package com.sequenceiq.cloudbreak.cloud.azure.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.AsyncTaskExecutor;

import com.sequenceiq.cloudbreak.cloud.PlatformParametersConsts;
import com.sequenceiq.cloudbreak.cloud.azure.AzureResourceGroupMetadataProvider;
import com.sequenceiq.cloudbreak.cloud.azure.AzureUtils;
import com.sequenceiq.cloudbreak.cloud.azure.client.AzureClient;
import com.sequenceiq.cloudbreak.cloud.azure.context.AzureContext;
import com.sequenceiq.cloudbreak.cloud.azure.service.AzureResourceNameService;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.CloudResource;
import com.sequenceiq.cloudbreak.cloud.model.Group;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.InstanceTemplate;
import com.sequenceiq.cloudbreak.cloud.model.Volume;
import com.sequenceiq.cloudbreak.cloud.notification.PersistenceNotifier;
import com.sequenceiq.common.api.type.CommonStatus;
import com.sequenceiq.common.api.type.ResourceType;

@RunWith(MockitoJUnitRunner.class)
public class AzureVolumeResourceBuilderTest {
    private static final long PRIVATE_ID = 1L;

    private static final String STACK_CRN = "crn";

    @Mock
    private AzureContext context;

    @Mock
    private AuthenticatedContext auth;

    @Mock
    private Group group;

    @Mock
    private Image image;

    @Mock
    private CloudInstance cloudInstance;

    @Mock
    private InstanceTemplate instanceTemplate;

    @Mock
    private Volume volumeTemplate;

    @Mock
    private CloudContext cloudContext;

    @Mock
    private AzureClient azureClient;

    @InjectMocks
    private AzureVolumeResourceBuilder underTest;

    @Mock
    private AsyncTaskExecutor intermediateBuilderExecutor;

    @Mock
    private PersistenceNotifier resourceNotifier;

    @Mock
    private AzureUtils azureUtils;

    @Mock
    private AzureResourceNameService resourceNameService;

    @Mock
    private AzureResourceGroupMetadataProvider azureResourceGroupMetadataProvider;

    @Before
    public void setUp() {
        CloudResource cloudResource1 = mock(CloudResource.class);
        CloudResource cloudResource2 = mock(CloudResource.class);
        when(context.getComputeResources(PRIVATE_ID)).thenReturn(List.of(cloudResource1, cloudResource2));
        when(context.getStringParameter(PlatformParametersConsts.RESOURCE_CRN_PARAMETER)).thenReturn(STACK_CRN);
        when(group.getReferenceInstanceConfiguration()).thenReturn(cloudInstance);
        when(cloudInstance.getTemplate()).thenReturn(instanceTemplate);
        when(instanceTemplate.getVolumes()).thenReturn(List.of(volumeTemplate));
        when(auth.getCloudContext()).thenReturn(cloudContext);
        when(auth.getParameter(AzureClient.class)).thenReturn(azureClient);
        when(resourceNameService.resourceName(eq(ResourceType.AZURE_VOLUMESET), any(), any(), eq(PRIVATE_ID), eq(STACK_CRN))).thenReturn("someResourceName");
    }

    @Test
    public void testWhenComputeResourceIsNullThenNullReturns() {
        when(context.getComputeResources(anyLong())).thenReturn(null);

        List<CloudResource> result = underTest.create(context, PRIVATE_ID, auth, group, image);

        Assert.assertNull(result);
    }

    @Test
    public void testWhenComputeResourceIsEmptyThenNullReturns() {
        when(context.getComputeResources(anyLong())).thenReturn(Collections.emptyList());

        List<CloudResource> result = underTest.create(context, PRIVATE_ID, auth, group, image);

        Assert.assertNull(result);
    }

    @Test
    public void testWhenDetachedReattachableVolumeExistsThenItShouldReturn() {
        CloudResource volumeSetResource = CloudResource.builder().type(ResourceType.AZURE_VOLUMESET).status(CommonStatus.DETACHED)
                .name("volume").params(Map.of()).build();
        CloudResource newInstance = CloudResource.builder().instanceId("instanceid").type(ResourceType.AZURE_INSTANCE).status(CommonStatus.CREATED)
                .name("instance").params(Map.of()).build();
        when(context.getComputeResources(PRIVATE_ID)).thenReturn(List.of(volumeSetResource, newInstance));

        List<CloudResource> result = underTest.create(context, PRIVATE_ID, auth, group, image);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(volumeSetResource, result.get(0));
    }

    @Test
    public void testWhenReattachableVolumeWithInstanceIdExistsThenItShouldReturn() {
        CloudResource volumeSetResource = CloudResource.builder().type(ResourceType.AZURE_VOLUMESET).status(CommonStatus.CREATED).instanceId("instanceid")
                .name("volume").params(Map.of()).build();
        CloudResource newInstance = CloudResource.builder().instanceId("instanceid").type(ResourceType.AZURE_INSTANCE).status(CommonStatus.CREATED)
                .name("instance").params(Map.of()).build();
        when(context.getComputeResources(PRIVATE_ID)).thenReturn(List.of(volumeSetResource, newInstance));

        List<CloudResource> result = underTest.create(context, PRIVATE_ID, auth, group, image);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(volumeSetResource, result.get(0));
    }

    @Test
    public void testWhenReattachableDoesNotExistsThenNewlyBuildedInstanceShouldBeCreated() {
        CloudResource volumeSetResource = CloudResource.builder().type(ResourceType.AZURE_VOLUMESET).status(CommonStatus.CREATED)
                .name("volume").params(Map.of()).build();
        CloudResource newInstance = CloudResource.builder().instanceId("instanceid").type(ResourceType.AZURE_INSTANCE).status(CommonStatus.CREATED)
                .name("instance").params(Map.of()).build();
        when(context.getComputeResources(PRIVATE_ID)).thenReturn(List.of(volumeSetResource, newInstance));

        List<CloudResource> result = underTest.create(context, PRIVATE_ID, auth, group, image);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotEquals(volumeSetResource, result.get(0));
    }
}
