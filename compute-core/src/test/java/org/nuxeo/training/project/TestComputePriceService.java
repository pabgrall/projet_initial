package org.nuxeo.training.project;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("org.nuxeo.training.project.ComputePrice-core")
public class TestComputePriceService {

    @Inject
    protected ComputePriceService computepriceservice;

    @Test
    public void testService() {
        assertNotNull(computepriceservice);
    }
}
