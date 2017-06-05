package org.nuxeo.training.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.RuntimeHarness;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
// @LocalDeploy("org.nuxeo.training.project.computeprice-core:computeprice-extension.xml")
// @PartialDeploy(bundle = "studio.extensions.pabgrall-SANDBOX", features = {
// TargetExtensions.Automation.class })
// @LocalDeploy({"org.nuxeo.training.project.computeprice-core:computepriceop-operation-contrib.xml",
// "org.nuxeo.training.project.computeprice-core:computeprice-service.xml"})
@LocalDeploy("org.nuxeo.training.project.ComputePrice-core:computeprice-extension.xml")
@Deploy({ "org.nuxeo.training.project.ComputePrice-core", "studio.extensions.pabgrall-SANDBOX",
        "org.nuxeo.ecm.core.cache"
        /*
         * "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.automation.features", "org.nuxeo.ecm.platform.query.api",
         * "org.nuxeo.runtime.management", "org.nuxeo.ecm.platform.types.core", "org.nuxeo.ecm.platform.api",
         * "org.nuxeo.ecm.platform.content.template", "org.nuxeo.ecm.platform.dublincore",
         * "org.nuxeo.ecm.platform.usermanager.api", "org.nuxeo.ecm.platform.usermanager", "org.nuxeo.ecm.core.io",
         * "org.nuxeo.ecm.platform.query.api", "org.nuxeo.ecm.platform.test:test-usermanagerimpl/directory-config.xml"
         */ })
public class TestComputePriceOp {

    @Inject
    protected CoreSession session;

    @Inject
    protected RuntimeHarness runtimeHarness;

    @Inject
    protected EventService eventService;

    @Inject
    protected AutomationService automationService;
    /*
     * @Test public void shouldCallTheOperation() throws OperationException { OperationContext ctx = new
     * OperationContext(session); DocumentModel doc = (DocumentModel) automationService.run(ctx, ComputePrice.ID);
     * assertEquals("/", doc.getPathAsString()); }
     */

    @Test
    public void shouldCallWithParameters() throws OperationException {

        // final String path = "/default-domain";
        //
        // // Map<String, Object> params = new HashMap<>();
        //
        // OperationContext ctx = new OperationContext(session);
        //
        // DocumentModel doc = createDocType(session, path, "myProduct",
        // "Products");
        // if (doc!=null) {
        // doc = session.createDocument(doc);
        // } else {
        // (new Exception("Ooooops!!!")).printStackTrace();
        // }
        // if (doc == null) {
        // (new Exception("Ooooooooooooooops!!!")).printStackTrace();
        // }
        //
        // assertEquals("Products",doc.getType());
        //
        // doc.setPropertyValue("price", "2.0");
        //
        // ctx.setInput(doc);
        //
        // doc = (DocumentModel) automationService.run(ctx,
        // "Document.ComputePriceOp"/* , params*/);
        // assertNotNull(doc);
        // assertEquals(path+"/Products", doc.getPathAsString());
        // assertEquals("StringProperty(product_schema:price*=2.4)",
        // doc.getProperty("price").toString());
        //
        //
        // DocumentModelList dml = new DocumentModelListImpl();
        //
        // DocumentModel doc2 = createDocType(session, path, "myProduct2",
        // "Products");
        // doc2.setPropertyValue("price", "3.0");
        // doc.setPropertyValue("price", "4.0");
        //
        // dml.add(doc);
        // dml.add(doc2);
        //
        // ctx.setInput(dml);
        //
        // dml = (DocumentModelList) automationService.run(ctx,
        // "Document.ComputePriceOp"/*, params*/);
        // assertNotNull(dml);
        // assertEquals(dml.totalSize(), 2);
        // assertEquals(dml.get(0).getPropertyValue("price"), (new
        // Float(4.8f)).toString());
        // assertEquals(((String)(dml.get(1).getPropertyValue("price"))).substring(0,3),
        // (new Float(3.6f)).toString());
        // }
        //
        // private DocumentModel createDocType(CoreSession session, String path,
        // String name, String type) {
        // DocumentModel doc = session.createDocumentModel(path,type,type);
        // doc.setPropertyValue("dc:title", name);
        // // doc.setPropertyValue("file:content", (Serializable) content);
        // // doc.setPropertyValue("file:filename",filename);
        // return doc;
    }
}
