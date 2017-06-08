package org.nuxeo.training.project;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.RuntimeHarness;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({
        "org.nuxeo.training.project.compute-core" /*
                                                   * , "org.nuxeo.ecm.platform.types.api",
                                                   * "org.nuxeo.ecm.platform.types.core", "org.nuxeo.ecm.platform.api",
                                                   * "org.nuxeo.ecm.platform.content.template",
                                                   * "org.nuxeo.ecm.platform.dublincore",
                                                   * "org.nuxeo.ecm.platform.usermanager.api",
                                                   * "org.nuxeo.ecm.platform.usermanager", "org.nuxeo.ecm.core.io",
                                                   * "org.nuxeo.ecm.platform.query.api",
                                                   * "org.nuxeo.ecm.platform.test:test-usermanagerimpl/directory-config.xml"
                                                   */ })

public class TestComputePrice {

    @Inject
    protected CoreSession session;

    @Inject
    protected RuntimeHarness runtimeHarness;

    @Inject
    protected EventService eventService;

    @Inject
    protected ComputePriceService computeprice;

    @Test
    public void testService() {
        Product prod = new Product();
        prod.setPath("/mypath");
        prod.setName("myname");
        prod.setPrice(0.0f);

        computeprice = Framework.getService(ComputePriceService.class);
        float price = computeprice.computePrice(prod);

        assertNotNull(computeprice);
        assertEquals(new Float(price).toString(), new Float(7.0f).toString());
    }

    @Test
    @LocalDeploy("org.nuxeo.training.project.compute-core:computeprice-extension.xml")
    public void testServiceLocal() {
        Product prod = new Product();
        prod.setPath("/mypath");
        prod.setName("myname");
        prod.setPrice(1.0f);

        computeprice = Framework.getService(ComputePriceService.class);
        float price = computeprice.computePrice(prod);

        assertNotNull(computeprice);
        assertEquals(new Float(price).toString(), new Float(1.2f).toString());
    }
}
