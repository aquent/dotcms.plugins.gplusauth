package com.aquent.dotcms.plugins.gplusauth;


import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Logger;

import org.apache.felix.http.api.ExtHttpService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @author Aquent, LLC (cfalzone@aquent.com)
 *
 */
public class GPlusAuthActivator extends GenericBundleActivator {

	private GPlusAuthRedirect gPlusAuthRedirect;
	private ServiceTracker<ExtHttpService, ExtHttpService> tracker;
	
    @Override
    public void start ( BundleContext bundleContext ) throws Exception {

        //Initializing services...
        initializeServices( bundleContext );
        
        // Start the Servlets
    	registerServlets( bundleContext );

        //Registering the ViewTool service
        registerViewToolService( bundleContext, new GPlusToolInfo() );
    }
    
    private void registerServlets( BundleContext ctx) {
		tracker = new ServiceTracker<ExtHttpService, ExtHttpService>(ctx, ExtHttpService.class, null) {
			@Override public ExtHttpService addingService(ServiceReference<ExtHttpService> reference) {
				ExtHttpService extHttpService = super.addingService(reference);

				gPlusAuthRedirect = new GPlusAuthRedirect();
			
				try {

					extHttpService.registerServlet("/oauth2callback", gPlusAuthRedirect, null, null);
					
				} catch (Exception e) {
					throw new RuntimeException("Failed to register servlets", e);
				}
				
				// Add the Servlets to the Exclude list
		    	CMSFilter.addExclude("/app/oauth2callback");
		    	
				Logger.info(this, "Registered servlets");

				return extHttpService;
			}
			@Override public void removedService(ServiceReference<ExtHttpService> reference, ExtHttpService extHttpService) {
				// Remove servlet Excludes from the list
		    	CMSFilter.removeExclude("/app/oauth2callback");
		    	
				extHttpService.unregisterServlet(gPlusAuthRedirect);
				
				super.removedService(reference, extHttpService);
			}
		};
		tracker.open();
    }

    @Override
    public void stop ( BundleContext bundleContext ) throws Exception {
        // Unregister the view tools
    	unregisterViewToolServices();
        
        // Stop the Servlets
    	tracker.close();
    	
    	// Unregister all the bundle services
        unregisterServices( bundleContext );
    }

}
