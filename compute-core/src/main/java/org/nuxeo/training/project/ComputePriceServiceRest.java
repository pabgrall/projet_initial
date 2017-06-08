package org.nuxeo.training.project;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.rest.*;
import org.nuxeo.ecm.webengine.model.*;
import org.nuxeo.ecm.webengine.model.impl.*;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.ecm.webengine.model.exceptions.*;
import org.nuxeo.ecm.webengine.*;
import org.nuxeo.ecm.webengine.app.WebEngineModule;

@WebObject(type = "File")
@Produces({ "text/html" })
@Path("/sampleCP")
public class ComputePriceServiceRest extends ModuleRoot {

    /**
     * Web Module Main Resource Sample.
     * <p>
     * This demonstrates how to define the entry point for a WebEngine module.
     * <p>
     * The module entry point is a regular JAX-RS resource named 'Sample1' and with an additional @WebModule annotation.
     * This annotation is mainly used to specify the WebModule name. I will explain the rest of @WebModule attributes in
     * the following samples. A Web Module is implicitly defined by its entry point. You can also configure a Web Module
     * using a module.xml file located in the module root directory. This file can be used to define: root resources (as
     * we've seen in the previous example), links, media type IDs random extensions to other extension points; but also
     * to define new Web Modules without an entry point.
     * <p>
     * A Web Module's Main resource is the entry point to the WebEngine model build over JAX-RS resources. If you want
     * to benefit of this model you should define such a module entry point rather than using plain JAX-RS resources.
     * <p>
     * This is a very simple module example, that prints the "Hello World!" message.
     *
     * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
     */

    private String updatePrice(String id) {
        if (path == null) {
            return "Try with a Products ID";
        }

        CoreSession session = ctx.getCoreSession();

        DocumentModel doc = null;

        try {
            doc = session.getDocument((DocumentRef) new IdRef(id));
        } catch (DocumentNotFoundException dnfe) {
            return "Document not found with ID " + id;
        }

        if (doc == null) {
            return "Ouch! null !";
        }

        if (!doc.getType().equals("Products")) {
            return "this is not a Products type";
        }

        ComputePriceService myComputePrice = (ComputePriceService) Framework.getService(ComputePriceService.class);

        Product product = new Product();
        product.setPath(doc.getPathAsString());
        product.setName(doc.getName());
        if (doc.getPropertyValue("price") != null) {
            product.setPrice(Float.parseFloat((String) doc.getPropertyValue("price")));
        }

        float price = myComputePrice.computePrice(product);
        doc.setPropertyValue("price", (new Float(price)).toString());
        session.saveDocument(doc);
        return "document updated to " + price;

    }

    @GET
    public String doGet() {
        return updatePrice(null);
    }

    @GET
    @Path("{path}")
    public String doGet(@PathParam("path") String path) {
        return updatePrice(path);
    }

    /*
     * @Override public Object handleError(WebApplicationException e) { if (e instanceof WebSecurityException) { return
     * Response.status(401).entity("not authorized").type("text/plain").build(); } else if (e instanceof
     * WebResourceNotFoundException) { return Response.status(404).entity(e.getMessage()).type("text/plain").build(); }
     * else { return super.handleError(e); } }
     */
}
