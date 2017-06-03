package org.nuxeo.training.project;

import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.training.project.ProductsAdapter;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "org.nuxeo.training.project.ComputePrice-core", "studio.extensions.pabgrall-SANDBOX"})
public class TestProductsAdapter {
  @Inject
  CoreSession session;

  @Test
  public void shouldCallTheAdapter() {
    String doctype = "Products";
    String testTitle = "My Adapter Title";

    DocumentModel doc = session.createDocumentModel("/", "test-adapter", doctype);
    ProductsAdapter adapter = doc.getAdapter(ProductsAdapter.class);
    adapter.setTitle(testTitle);
    adapter.setProductName("myProduct");
    adapter.setPrice("10.0");
    adapter.setDistributor("NUXEO", "Paris");
    adapter.create();
    // session.save() is only needed in the context of unit tests
    session.save();
    Map<String,String> m = adapter.getDistributor();
    
    Assert.assertEquals("NUXEO",  m.get("name"));
    Assert.assertEquals("Paris",  m.get("sell_location"));

    Assert.assertNotNull("The adapter can't be used on the " + doctype + " document type", adapter);
    Assert.assertEquals("Document title does not match when using the adapter", testTitle, adapter.getTitle());
  }
}
