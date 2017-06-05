package org.nuxeo.training.project;

public interface ComputePriceService {

    public float computePrice(Product p);

    public void setCustom(String s);

    public String getCustom();
}
