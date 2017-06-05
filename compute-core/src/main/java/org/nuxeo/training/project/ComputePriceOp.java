package org.nuxeo.training.project;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentRefList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id = ComputePriceOp.ID, category = Constants.CAT_DOCUMENT, label = "Document.ComputePrice", description = "Describe here what your operation does.")
public class ComputePriceOp {

    public static final String ID = "Document.ComputePriceOp";

    @Context
    protected OperationContext operationContext;

    @Context
    protected CoreSession session;

    private static final Log log = LogFactory.getLog(ComputePriceOp.class);

    /*
     * @Param(name = "name", required = false) protected String name;
     */

    static String customization = null;

    final String myConstrainedType = "Products";

    @OperationMethod
    public DocumentModel run(DocumentModel doc) throws Exception {

        if (doc == null || !doc.getType().equals(myConstrainedType)) {
            return doc;
        }
        /*
         * if (name == null) { name = "Untitled"; }
         */

        String path = doc.getPathAsString();

        // DocumentModel newDoc = session.createDocumentModel(
        // doc.getPathAsString(), name, myConstrainedType);
        /*
         * if (content != null) { DocumentHelper.setProperties(session, newDoc, content); }
         */

        /* float price = computePrice(path, doc); */
        ComputePriceService myComputePrice = (ComputePriceService) Framework.getService(ComputePriceService.class);

        Product product = new Product();
        product.setPath(path);
        product.setName(doc.getName());
        if (doc.getPropertyValue("price") != null) {
            product.setPrice(Float.parseFloat((String) doc.getPropertyValue("price")));
        }

        float price = myComputePrice.computePrice(product);
        doc.setPropertyValue("price", (new Float(price)).toString());
        return doc;
    }

    @OperationMethod
    public DocumentModelList run(DocumentModelList docs) throws Exception {
        DocumentModelListImpl result = new DocumentModelListImpl((int) docs.totalSize());
        for (DocumentModel doc : docs) {
            result.add(run(doc));
        }
        return result;
    }

    @OperationMethod
    public DocumentModelList run(DocumentRefList docs) throws Exception {
        DocumentModelListImpl result = new DocumentModelListImpl((int) docs.totalSize());
        for (DocumentRef doc : docs) {
            result.add(run(session.getDocument(doc)));
        }
        return result;
    }
}
