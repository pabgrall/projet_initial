package org.nuxeo.training.project;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 *
 */
public class ProductsAdapter {
    protected final DocumentModel doc;

    protected String titleXpath = "dc:title";

    protected String descriptionXpath = "dc:description";

    public ProductsAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    // Basic methods
    //
    // Note that we voluntarily expose only a subset of the DocumentModel API in
    // this adapter.
    // You may wish to complete it without exposing everything!
    // For instance to avoid letting people change the document state using your
    // adapter,
    // because this would be handled through workflows / buttons / events in
    // your application.
    //
    public void create() {
        CoreSession session = doc.getCoreSession();
        session.createDocument(doc);
    }

    public void save() {
        CoreSession session = doc.getCoreSession();
        session.saveDocument(doc);
    }

    public DocumentRef getParentRef() {
        return doc.getParentRef();
    }

    // Technical properties retrieval
    public String getId() {
        return doc.getId();
    }

    public String getName() {
        return doc.getName();
    }

    public String getPath() {
        return doc.getPathAsString();
    }

    public String getState() {
        return doc.getCurrentLifeCycleState();
    }

    // Metadata get / set
    public String getTitle() {
        return doc.getTitle();
    }

    public void setTitle(String value) {
        doc.setPropertyValue(titleXpath, value);
    }

    public String getDescription() {
        return (String) doc.getPropertyValue(descriptionXpath);
    }

    public void setDescription(String value) {
        doc.setPropertyValue(descriptionXpath, value);
    }

    // product_schema
    /*
     * availability // Boolean !!! category // Directory !!! name origin price size
     */
    // Products
    /*
     * delivery_time // Date !!! distributor.name distributor.sell_location
     */
    public void setAvailability(boolean b) {
        doc.setPropertyValue("product_schema:availability", b);
    }

    public Boolean getAvailability() {
        return (Boolean) doc.getPropertyValue("product_schema:availability");
    }

    public void setCategory(String c) {
        doc.setPropertyValue("product_schema:category", c);
    }

    public String getCategory() {
        return (String) doc.getPropertyValue("product_schema:category");
    }

    public void setProductName(String n) {
        doc.setPropertyValue("product_schema:name", n);
    }

    public String getProductName() {
        return (String) doc.getPropertyValue("product_schema:name");
    }

    public void setOrigin(String n) {
        doc.setPropertyValue("product_schema:origin", n);
    }

    public String getOrigin() {
        return (String) doc.getPropertyValue("product_schema:origin");
    }

    public void setPrice(String n) {
        doc.setPropertyValue("product_schema:price", n);
    }

    public String getPrice() {
        return (String) doc.getPropertyValue("product_schema:price");
    }

    public void setSize(String n) {
        doc.setPropertyValue("product_schema:size", n);
    }

    public String getSize() {
        return (String) doc.getPropertyValue("product_schema:size");
    }

    public void setDeliverytTime(Date d) {
        doc.setPropertyValue("product:delivery_time", d);
    }

    public String getDeliverytTime() {
        return (String) doc.getPropertyValue("product:delivery_time");
    }

    public void setDistributor(String n, String sl) {
        /*
         * String[] m2 = doc.getSchemas(); // MISSING distributor... for (String s: m2) { Map<String, Object> m1 =
         * doc.getProperties(s); // MISSING distributor... System.out.println(s); System.out.println(m1.keySet()); }
         */
        doc.setPropertyValue("product:distributor/name", n);
        doc.setPropertyValue("product:distributor/sell_location", sl);
    }

    public Boolean getSellState() {
        return (Boolean) doc.getPropertyValue("product:sell_state");
    }

    public void setSellState(Boolean b) {
        doc.setPropertyValue("product:sell_state", b);
    }

    public HashMap<String, String> getDistributor() {
        HashMap<String, String> mymap = new HashMap<String, String>();
        mymap.put("name", (String) doc.getPropertyValue("product:distributor/name"));
        mymap.put("sell_location", (String) doc.getPropertyValue("product:distributor/sell_location"));
        return mymap;
    }
}
