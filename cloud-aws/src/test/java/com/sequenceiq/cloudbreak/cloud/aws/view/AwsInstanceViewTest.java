package com.sequenceiq.cloudbreak.cloud.aws.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.sequenceiq.cloudbreak.cloud.model.InstanceStatus;
import com.sequenceiq.cloudbreak.cloud.model.InstanceTemplate;

public class AwsInstanceViewTest {

    @Test
    public void testTemplateParameters() {
        Map<String, Object> map = new HashMap<>();
        map.put("encrypted", true);
        map.put("type", "CUSTOM");

        InstanceTemplate instanceTemplate = new InstanceTemplate("", "", 0L, Collections.emptyList(), InstanceStatus.STARTED,
                map, 0L, "imageId");

        AwsInstanceView actual = new AwsInstanceView(instanceTemplate);

        Assert.assertTrue(actual.isKmsCustom());
    }

    @Test
    public void testOnDemand() {
        Map<String, Object> map = new HashMap<>();
        map.put("spotPercentage", 30);
        InstanceTemplate instanceTemplate = new InstanceTemplate("", "", 0L, Collections.emptyList(), InstanceStatus.STARTED,
                map, 0L, "imageId");
        AwsInstanceView actual = new AwsInstanceView(instanceTemplate);
        assertEquals(70, actual.getOnDemandPercentage());
    }

    @Test
    public void testOnDemandMissingPercentage() {
        InstanceTemplate instanceTemplate = new InstanceTemplate("", "", 0L, Collections.emptyList(), InstanceStatus.STARTED,
                Map.of(), 0L, "imageId");
        AwsInstanceView actual = new AwsInstanceView(instanceTemplate);
        assertEquals(100, actual.getOnDemandPercentage());
    }

    @Test
    public void testSpotMaxPrice() {
        Map<String, Object> map = new HashMap<>();
        Double spotMaxPrice = Double.valueOf(0.9);
        map.put("spotMaxPrice", spotMaxPrice);
        InstanceTemplate instanceTemplate = new InstanceTemplate("", "", 0L, Collections.emptyList(), InstanceStatus.STARTED,
                map, 0L, "imageId");
        AwsInstanceView actual = new AwsInstanceView(instanceTemplate);
        assertEquals(spotMaxPrice, actual.getSpotMaxPrice());
    }

    @Test
    public void testMissingSpotMaxPrice() {
        InstanceTemplate instanceTemplate = new InstanceTemplate("", "", 0L, Collections.emptyList(), InstanceStatus.STARTED,
                Map.of(), 0L, "imageId");
        AwsInstanceView actual = new AwsInstanceView(instanceTemplate);
        assertNull(actual.getSpotMaxPrice());
    }
}
