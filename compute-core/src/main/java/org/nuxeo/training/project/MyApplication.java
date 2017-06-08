package org.nuxeo.training.project;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class MyApplication extends Application {

    public MyApplication() {
        super();
    }

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> result = new HashSet<Class<?>>();
        result.add(ComputePriceServiceRest.class);
        return result;
    }

}
