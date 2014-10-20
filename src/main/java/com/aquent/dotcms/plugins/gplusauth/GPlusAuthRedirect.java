package com.aquent.dotcms.plugins.gplusauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * Google OAuth 2 Callback servlet
 * Mapped to /app/oauth2callback
 * 
 * @author Aquent, LLC (cfalzone@aquent.com)
 *
 */
public class GPlusAuthRedirect extends HttpServlet {

	private static final long serialVersionUID = -6307356633485375168L;
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	
	private Host defaultHost = null;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		// Generate the redirect url to return to
		String redirectUrl = UtilMethods.isSet((String) session.getAttribute("gPlusRedirect")) ? 
				(String) session.getAttribute("gPlusRedirect") : "/";
		if(redirectUrl.contains("?")) redirectUrl = redirectUrl+"&";
		else redirectUrl = redirectUrl+"?";
		
		// Get the url for this page for the gplus api
		String hostName = req.getServerPort() == 80 ?  req.getServerName() : req.getServerName()+":"+req.getServerPort();
		String redirectUri = "http://"+hostName+"/app/oauth2callback";
		
		// Check if already connected to G+
		GoogleTokenResponse tokenData = (GoogleTokenResponse) session.getAttribute("gPlusToken");
		if (tokenData != null) {
			resp.sendRedirect(redirectUrl+"s=pass");
			return;
		}
		
		// Prevent Request Forgery by verifying state from session = state from request
		if (!req.getParameter("state").equals(session.getAttribute("gPlusState"))) {
			Logger.info(this, "State missmatch.  Request="+req.getParameter("state")+"  -  Session="+session.getAttribute("gPlusState"));
			resp.sendRedirect(redirectUrl+"s=fail&error=state");
			return;
		}
		
		// Remove state
		session.removeAttribute("gPlusState");
		
		// Get Google's response
		String code = req.getParameter("code");
		Logger.info(this, "Code = "+code);
		
		try {
			String gpClientId = getDefaultHost().getStringProperty("gplusClientId");
			String gpClientSecret = getDefaultHost().getStringProperty("gplusClientSecret");
			
			GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
					TRANSPORT, JSON_FACTORY, 
					gpClientId, gpClientSecret,
					code, redirectUri).execute();
			
			// Logged in, set up the session
			GoogleIdToken idToken = tokenResponse.parseIdToken();
	        String gPlusId = idToken.getPayload().getSubject();
	        String gPlusEmail = idToken.getPayload().getEmail();
	        
	        Logger.info(this, "gPlus Login from "+gPlusId+" with email "+gPlusEmail);
	        
	        session.setAttribute("gPlusId", gPlusId);
	        session.setAttribute("gPlusEmail", gPlusEmail);
	        session.setAttribute("gPlusToken", tokenResponse);
	        
	        resp.sendRedirect(redirectUrl+"s=pass");
		} catch (Exception e) {
			Logger.error(this, "Error Connecting with Google Plus", e);
			resp.sendRedirect(redirectUrl+"s=fail&error=exception");
		}
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
