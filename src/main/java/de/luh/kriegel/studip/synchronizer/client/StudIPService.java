package de.luh.kriegel.studip.synchronizer.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.auth.Credentials;
import de.luh.kriegel.studip.synchronizer.config.Endpoints;
import de.luh.kriegel.studip.synchronizer.config.SubPaths;


public class StudIPService {

	Logger log = LogManager.getLogger(StudIPService.class);

	private URI baseUri;
	private BasicHttpClient httpClient;
	
	private boolean isAuthenticated = false;

	public StudIPService(URI baseUri, Credentials credentials) {
		this.baseUri = baseUri;

		this.httpClient = new BasicHttpClient(baseUri, credentials);
	}

	public boolean authenticate() {
		HttpResponse response;
		try {
			response = httpClient.get(SubPaths.API.toString() + Endpoints.USER.toString());
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
		
		if (response.getStatusLine().getStatusCode() % 100 == 2) {
			return true;
		}

		return false;
	}
	
	public List<String> getCourses() {
		
		
		return null;
	}
	
	

}
