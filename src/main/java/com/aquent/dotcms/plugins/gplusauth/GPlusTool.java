package com.aquent.dotcms.plugins.gplusauth;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.util.Logger;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

/**
 * 
 * A Viewtool to generate a Google+ Login Url
 * 
 * @author Aquent, LLC (cfalzone@aquent.com)
 *
 */
public class GPlusTool implements ViewTool {

	private Host defaultHost = null;
	
	@Override
	public void init(Object initData) {
		Logger.info(this, "GPlus Viewtool Initialized");
	}
	
	/**
	 * Generates a G+ Auth Request URL
	 * 
	 * @param req  The http request to get the session from
	 * @return  The Auth Request URL to link the user to
	 * @throws Exception 
	 */
	public String getGPlusAuthUrl(HttpServletRequest req) throws Exception {
		// Construct the callback uri with the request's hostname
		String hostName = req.getServerPort() == 80 ?  req.getServerName() : req.getServerName()+":"+req.getServerPort();
		String redirectUri = "http://"+hostName+"/app/oauth2callback";
		
		// Generate the state to prevent request forgery
		String state = new BigInteger(130, new SecureRandom()).toString(32);
        req.getSession().setAttribute("gPlusState", state);
        
        // Save the url to the session so we can get back
        req.getSession().setAttribute("gPlusRedirect", req.getRequestURL().toString());
        
        // Get the Google App Client ID from the default host
        String gpClientId = getDefaultHost().getStringProperty("gplusClientId");
        
        Logger.info(this, "Generating G+ Login URL for "+req.getRequestURL().toString()+" on Client ID="+gpClientId);
        
        // Generate the Auth URL and return it to the page
		return new GoogleAuthorizationCodeRequestUrl(
				gpClientId, redirectUri, Arrays.asList(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/userinfo.profile")
            ).setState(state).setApprovalPrompt("force").build();
	}
	
	private Host getDefaultHost() throws Exception {
		if(defaultHost == null) {
			// Get the default host
			try {
				defaultHost = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);
			} catch (Exception e) {
				Logger.error(this, "Unable to get the default host", e);
				throw new Exception("Unable to get the default host", e);
			}
		}
		
		return defaultHost;
	}

}
