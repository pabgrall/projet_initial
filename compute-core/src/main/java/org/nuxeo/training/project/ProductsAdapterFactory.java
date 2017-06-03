package org.nuxeo.training.project;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class ProductsAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if ("Products".equals(doc.getType()) && doc.hasSchema("dublincore")){
            return new ProductsAdapter(doc);
        }else{
            return null;
        }
    }
}
