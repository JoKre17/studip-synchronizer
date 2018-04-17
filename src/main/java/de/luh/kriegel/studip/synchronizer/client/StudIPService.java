package de.luh.kriegel.studip.synchronizer.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.auth.Credentials;
import de.luh.kriegel.studip.synchronizer.config.Endpoints;
import de.luh.kriegel.studip.synchronizer.config.SubPaths;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.Id;
import de.luh.kriegel.studip.synchronizer.content.model.data.User;

public class StudIPService {

	Logger log = LogManager.getLogger(StudIPService.class);

	private URI baseUri;
	private BasicHttpClient httpClient;

	private boolean isAuthenticated = false;
	private Id user_id = null;

	public StudIPService(URI baseUri, Credentials credentials) {
		this.baseUri = baseUri;

		this.httpClient = new BasicHttpClient(baseUri, credentials);
	}

	private String getResponseBody(HttpResponse response) {
		assert response != null;

		String responseBody;
		try {
			HttpEntity entity = response.getEntity();

			responseBody = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return responseBody;
	}

	public boolean authenticate() {
		HttpResponse response;
		try {
			response = httpClient.get(SubPaths.API.toString() + Endpoints.USER.toString());
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			isAuthenticated = false;
			return false;
		}

		if (response.getStatusLine().getStatusCode() / 100 == 2) {
			isAuthenticated = true;
			
			String responseBody = getResponseBody(response);
			log.debug(responseBody);
			
			try {
				User user = User.fromJson((JSONObject) new JSONParser().parse(responseBody));
				user_id = user.getId();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return true;
		}

		isAuthenticated = false;
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Course> getCourses() throws Exception {
		if (!isAuthenticated) {
			throw new Exception("Not Authenticated");
		}

		HttpResponse response;
		try {
			response = httpClient.get(
					SubPaths.API.toString() + Endpoints.USER_COURSES.toString().replace(":user_id", user_id.asHex()));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if (response.getStatusLine().getStatusCode() / 100 == 2) {
			String responseBody = getResponseBody(response);
			JSONObject responseJson = (JSONObject) new JSONParser().parse(responseBody);

			log.debug(responseBody);
			
			List<Course> courses = new ArrayList<>();

			if (responseJson.containsKey("collection")) {
				((Map<String, JSONObject>) responseJson.get("collection")).forEach((link, courseJson) -> {
					Course course = Course.fromJson(courseJson);

					courses.add(course);
				});
			}

			return courses;
		}

		return null;
	}

}
