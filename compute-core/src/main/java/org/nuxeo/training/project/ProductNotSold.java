package org.nuxeo.training.project;


import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

public class ProductNotSold implements EventListener {
  
	private static final Log log = LogFactory.getLog(ProductNotSold.class);
	
	private final String HiddenFolderXPath = "/default-domain/hidden"; // ONLY FOR TESTING !!!
	
	private final String docType = "Products";
	private final String picture = "Visual";
	
	@Override
    public void handleEvent(Event event) {
    	
    	log.debug("triggered");
    	
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
          return;
        }

        DocumentEventContext docCtx = (DocumentEventContext) ctx;
        DocumentModel doc = docCtx.getSourceDocument();
        
        if (doc == null) {
        	log.debug("doc is null");
        	return;
        }
        
        if (!doc.getType().equals(docType)) {
        	log.debug("Not the right type");
        	return;
        }
        ProductsAdapter adapter = doc.getAdapter(ProductsAdapter.class);
        Boolean docsold = adapter.getSellState();
        if (docsold) {
        	log.debug("Product still sold");
        	return;
        }

        // Add some logic starting from here.
        String name = doc.getName();
        DocumentRef source = doc.getRef();
        
             
        DocumentModel doc1 = ctx.getCoreSession().createDocumentModel("/default-domain", "hidden", "Folder");
        doc1 = ctx.getCoreSession().getDocument(doc1.getRef());
        
        DocumentRef target = doc1.getRef();
        
        log.debug("Hidden folder found");
        
        
        
        CollectionManager cm = Framework.getLocalService(CollectionManager.class);
        log.debug("Document is a collection : "+cm.isCollection(doc));

        Collection colladapter = doc.getAdapter(Collection.class);
        List<String> ids = colladapter.getCollectedDocumentIds();
        
        
        log.debug("ids size is "+ids.size());
        if (ids.size() != 0) {
        	// boolean changed = false;

	        for (String id : ids) {
	        	
	        	DocumentModel dm = ctx.getCoreSession().getDocument(new IdRef(id));
	        	        			
	        	log.debug("dm  type is "+dm.getType()+" and name "+dm.getName());
	        	if (dm.getType().equals(picture)) {
	        		log.debug("moving file "+dm.getName()+" / "+dm.getPathAsString()+" into hidden folder");
	        		ctx.getCoreSession().move(dm.getRef(), target, dm.getName());
	        		log.debug("document moved");
	        		// changed = true;
	        	}
	        }
	        /*
	        if (changed) {
		        log.debug("saving documents Product "+doc1.getName());
		        ctx.getCoreSession().saveDocument(doc1);
		        log.debug("Product saved");
	        }
	        */
	    }
	}
}
