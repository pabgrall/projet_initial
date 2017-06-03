package org.nuxeo.training.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Extension;

public class ComputePriceServiceImpl extends DefaultComponent implements ComputePriceService {

	private static final Log log = LogFactory.getLog(ComputePriceServiceImpl.class);
	
    private MyString customization = null;
    
    private String myConstrainedType = "Products";
	
	/**
     * Component activated notification.
     * Called when the component is activated. All component dependencies are resolved at that moment.
     * Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        // super.activate(context);
        customization = new MyString();
        // log.warn("YES I SAY YES");
        // Framework.getRuntime().getWarnings().add("YES I SAY YES");
    }

    /**
     * Component deactivated notification.
     * Called before a component is unregistered.
     * Use this method to do cleanup if any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        // super.deactivate(context);
    	customization = new MyString();
    	// log.warn("deactivating");
    }

    /**
     * Application started notification.
     * Called after the application started.
     * You can do here any initialization that requires a working application
     * (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) {
        // do nothing by default. You can remove this method if not used.
    	log.info("Application started");
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
    	// Add some logic here to handle contributions
    	log.info("in registerContribution; testing pricing");
    	// Framework.getRuntime().getWarnings().add("in registerContribution for pricing");
    	customization = (MyString) contribution;
        String tva = customization.tva;
    	
    	if ("pricing".equals(extensionPoint)) {
             log.info("pricing OK");
             // Framework.getRuntime().getWarnings().add("in registerContribution pricing OK");
         }
    	 
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
    	customization = null;
    }

    public void setCustom(String s) {
    	customization.tva = s;
    }
    
    public String getCustom() {
    	return customization.tva;
    }
    
    @Override
	public float computePrice(Product prod) {
    	
    	String path = prod.getPath();
    	
    	log.info(customization.tva);
    	
    	if (customization == null) {
    		if (path == null || path.equals("/")) {
    			return -1.0f;
    		} else {
    			float price = path.length();
    			return price;            
    		}
    	} else {
    		// HACK !!!
    		// String[] prices = customization;
    		
    		log.info("customization found = "+customization.tva);
    		Float tva = 0.0f;
    		if (customization.tva == null) {
    			tva = 1.0f;
    		} else {
    			tva = Float.parseFloat(customization.tva);
    		}
    		if (tva < 1.0f) {
    			tva = 1.0f+tva;
    		}
    		
    		if (tva != 1.0f) {
    			return tva*prod.getPrice();
    		}
    		
    		/*
    		for (String price: prices) {
    			String item = price.substring(price.indexOf("item=\"")+"item=\"".length());
    			if (item.length() == 0) {
    				continue;
    			}
    			item = item.substring(0, item.indexOf('"'));
    			String unitpricestr = price.substring(price.indexOf("unit=\"")+"unit=\"".length());
    			if (unitpricestr.length() == 0) {
    				continue;
    			}
    			unitpricestr = unitpricestr.substring(0, item.indexOf('"'));
    			float unitprice = Float.parseFloat(unitpricestr);
    			if (prod.getName().indexOf(item) != 0) {
    				return unitprice;
    			}
    		}  */
    		// default value in case not found in customization
    		float price = path.length();
			return price; 
    	}
    }
    
    /*
    @Override
    public void registerExtension(Extension extension) {
        // Add some logic here to handle contributions
    	log.error("in registerExtension; testing pricing");
    	Framework.getRuntime().getWarnings().add("in registerExtension for pricing - 1");
    	 if ("pricing".equals(extension.getExtensionPoint())) {
             log.error("pricing OK");
             Framework.getRuntime().getWarnings().add("in registerExtension for pricing - 2");
         }
    	 
    	 Object[] os = extension.getContributions();
    	 if (os != null && os.length > 0) {
    		 Framework.getRuntime().getWarnings().add("YES");
    		 for (Object o: os) {
    			 
    			 Framework.getRuntime().getWarnings().add("o.class="+o.getClass().toString());
    			 
		    	 if (o instanceof java.lang.String) {
		    		 customization = (String) o;
		    		 Framework.getRuntime().getWarnings().add("in registerExtension customization ="+customization);
		    	 } else {
		    		 Framework.getRuntime().getWarnings().add("in registerExtension not a String");
		    	 }
    		 }
    	 } else {
    		 Framework.getRuntime().getWarnings().add("os is null or has length 0");	     
    	 }
    }

    @Override
    public void unregisterExtension(Extension extension) {
        // Logic to do when unregistering any contribution
    	// customization = null;
    }

    */
    
}
