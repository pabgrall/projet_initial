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
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

public class ProductNotSold implements EventListener {

    private static final Log log = LogFactory.getLog(ProductNotSold.class);

    private String HiddenFolderXPath = "/default-domain/hidden"; // ONLY
                                                                 // FOR
                                                                 // TESTING
                                                                 // !!!

    private final String HiddenFolderXPath4Prod = "/default-domain/workspaces/hidden";

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

        DocumentRef target = null;

        // Trzying first unit test condition
        DocumentModel doc1 = ctx.getCoreSession().createDocumentModel("/default-domain", "hidden", "Workspace");
        
        try {
            doc1 = ctx.getCoreSession().getDocument(doc1.getRef());
            target = doc1.getRef();
        } catch (DocumentNotFoundException dnfe) {
            target = null; // We are not in test !!!
        }

        // if not in unit test condition, trying prod settings
        if (target == null) {
            DocumentModel doc1prod = ctx.getCoreSession().createDocumentModel("/default-domain/workspaces", "hidden", "Workspace");

            try {
                doc1 = ctx.getCoreSession().getDocument(doc1prod.getRef());
                target = doc1prod.getRef();
            } catch (DocumentNotFoundException dnfe) {
                target = null;
            }
        }

        if (target != null) {
            log.debug("Hidden folder found");
        } else {
            log.warn("ProductNotSold Event Handler : Hidden folder NOT found - returning with no action...");
            return;
        }
        
        log.debug(ctx.getCoreSession().getPrincipal().getName());

        CollectionManager cm = Framework.getLocalService(CollectionManager.class);
        log.debug("Document is a collection : " + cm.isCollection(doc));

        Collection colladapter = doc.getAdapter(Collection.class);
        List<String> ids = colladapter.getCollectedDocumentIds();

        log.debug("ids size is " + ids.size());
        if (ids.size() != 0) {
            // boolean changed = false;

            for (String id : ids) {

                DocumentModel dm = ctx.getCoreSession().getDocument(new IdRef(id));

                log.debug("dm  type is " + dm.getType() + " and name " + dm.getName());
                if (dm.getType().equals(picture)) {
                    log.debug("moving file " + dm.getName() + " / " + dm.getPathAsString() + " into hidden folder");
                    ctx.getCoreSession().move(dm.getRef(), target, dm.getName());
                    log.debug("document moved");
                    // changed = true;
                }
            }
            /*
             * if (changed) { log.debug("saving documents Product "+doc1.getName());
             * ctx.getCoreSession().saveDocument(doc1); log.debug("Product saved"); }
             */
        }
    }
}
