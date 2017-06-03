package org.nuxeo.training.project;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@Deploy({"org.nuxeo.training.project.ComputePrice-core", "org.nuxeo.ecm.platform.collections.core", "studio.extensions.pabgrall-SANDBOX",
	/* "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.automation.features", 
	"org.nuxeo.ecm.platform.query.api", "org.nuxeo.runtime.management", */
	"org.nuxeo.ecm.platform.types.core", "org.nuxeo.ecm.core.cache"
/*	"org.nuxeo.ecm.platform.api", "org.nuxeo.ecm.platform.content.template", 
	"org.nuxeo.ecm.platform.dublincore", "org.nuxeo.ecm.platform.usermanager.api", 
	"org.nuxeo.ecm.platform.usermanager", "org.nuxeo.ecm.core.io", 
	"org.nuxeo.ecm.platform.query.api", 
	"org.nuxeo.ecm.platform.test:test-usermanagerimpl/directory-config.xml" */ })

public class TestProductNotSold {

    protected final List<String> events = Arrays.asList("productnotsold", "documentModified");

    private static final Log log = LogFactory.getLog(TestProductNotSold.class);
    
    @Inject
    protected EventService s;
   
    @Inject
    CoreSession session;
    
    private final String HiddenFolderXPath = "/default-domain/hidden";
	
    private final String doctype = "Products";
    private final String picture = "Visual"; 
    
    @Test
    public void listenerRegistration() {
     
        log.debug("Before Folder creation");
        
        DocumentModel doc1 = session.createDocumentModel("/", "default-domain", "Folder");
        doc1 = session.createDocument(doc1);
        session.saveDocument(doc1);
        
        doc1 = session.createDocumentModel("/default-domain", "hidden", "Folder");
        doc1 = session.createDocument(doc1);
        session.saveDocument(doc1);
        
        log.debug("Before Products creation");
        
        
        DocumentModel doc2 = session.createDocumentModel("/default-domain", "test-notsold", doctype);
        doc2 = session.createDocument(doc2);
        ProductsAdapter adapter = doc2.getAdapter(ProductsAdapter.class);
        adapter.setPrice("50.0");
        adapter.setTitle("Inserted doc");
        adapter.setSellState(true);
        session.saveDocument(doc2);
        
        DocumentModel  doc3 = session.createDocumentModel("/default-domain/hidden", "test-notsold", doctype);
        assertNotNull(doc3);
        try {
        	doc3 = session.getDocument(doc3.getRef());
        } catch (DocumentNotFoundException dnfe) {
        	doc3 = null;
        }
        
        assertNull(doc3);    

        log.debug("before adding Visuals");
        
        DocumentModel docv1 = session.createDocumentModel("/default-domain", "mypicture1", picture);
        docv1 = session.createDocument(docv1);
        session.saveDocument(docv1);
        DocumentModel docv2 = session.createDocumentModel("/default-domain", "mypicture2", picture);
        docv2 = session.createDocument(docv2);
        session.saveDocument(docv2);
        CollectionManager cm = Framework.getLocalService(CollectionManager.class);
        assertNotNull(cm);
        cm.addToCollection(doc2, docv1, session);
        cm.addToCollection(doc2, docv2, session);
        session.saveDocument(doc2);
        
        log.debug(picture+"s added");
       
        log.debug("Before checking listener");
        
    	EventListenerDescriptor listener = s.getEventListener("productnotsold");
        assertNotNull(listener);
        assertTrue(events.stream().allMatch(listener::acceptEvent));
        
        log.debug("Before modifying doc");
        
        adapter.setSellState(false);
        session.saveDocument(doc2);
        /*        
        log.debug("Before triggering event"); 
        
        EventProducer eventProducer = Framework.getService(EventProducer.class);
        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), doc2);
        Event event = ctx.newEvent("productnotsold");
        eventProducer.fireEvent(event);
        */
        try {
        	Thread.sleep(1000);
        } catch (InterruptedException ie) {}
        
        log.debug("Before checking document move");
        
        DocumentModel doc4 = session.createDocumentModel("/default-domain/hidden", "mypicture1", picture);
        try {
        	doc4 = session.getDocument(doc4.getRef());
        } catch (DocumentNotFoundException dnfe) {
        	doc4 = null;
        }
        
        assertNotNull(doc4);
        assertEquals(doc4.getPathAsString(),HiddenFolderXPath+"/mypicture1");
    
        doc4 = session.createDocumentModel("/default-domain/hidden", "mypicture2", picture);
        try {
        	doc4 = session.getDocument(doc4.getRef());
        } catch (DocumentNotFoundException dnfe) {
        	doc4 = null;
        }
        
        assertNotNull(doc4);
        assertEquals(doc4.getPathAsString(),HiddenFolderXPath+"/mypicture2");
        
        // checking the collection size of the Product...
        
        doc3 = session.createDocumentModel("/default-domain", "test-notsold", doctype);
        assertNotNull(doc3);
        try {
        	doc3 = session.getDocument(doc3.getRef());
        } catch (DocumentNotFoundException dnfe) {
        	doc3 = null;
        }
        
        assertNotNull(doc3);    // Product still exists

        Collection colladapter = doc3.getAdapter(Collection.class);
        List<String> ids = colladapter.getCollectedDocumentIds();
        assertEquals(ids.size(),2);   // Collection size is STILL 2
    }    
}