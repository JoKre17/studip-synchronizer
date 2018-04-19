package de.luh.kriegel.studip.synchronizer.client.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.luh.kriegel.studip.synchronizer.client.BasicHttpClient;
import de.luh.kriegel.studip.synchronizer.config.Endpoints;
import de.luh.kriegel.studip.synchronizer.config.SubPaths;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;

public class CourseService {

	private static final Logger log = LogManager.getLogger(CourseService.class);

	private final BasicHttpClient httpClient;

	private final AuthService authService;

	public CourseService(BasicHttpClient httpClient, AuthService authService) {
		this.httpClient = httpClient;
		this.authService = authService;
	}

	/**
	 * Retrieves the courses of the current semester
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Course> getCourses() throws Exception {
		if (!authService.isAuthenticated()) {
			throw new Exception("Not Authenticated");
		}

		int totalAmountCourses = getAmountCourses();
		
		int limit = 20;

		List<Course> courses = new ArrayList<>();

		HttpResponse response;

		for (int offset = 0; offset < totalAmountCourses; offset += limit) {
			try {
				response = httpClient.get(SubPaths.API.toString()
						+ Endpoints.USER_COURSES.toString().replace(":user_id", authService.getCurrentUserId().asHex())
						+ "?offset=" + offset + "&limit=" + limit);
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
				return null;
			}

			if (response.getStatusLine().getStatusCode() / 100 == 2) {
				String responseBody = BasicHttpClient.getResponseBody(response);
				JSONObject responseJson = (JSONObject) new JSONParser().parse(responseBody);

				log.debug(responseBody);

				if (responseJson.containsKey("collection")) {
					((Map<String, JSONObject>) responseJson.get("collection")).forEach((link, courseJson) -> {
						Course course = Course.fromJson(courseJson);

						courses.add(course);
					});
				}
			}
		}

		return courses;
	}

	public int getAmountCourses() throws Exception {
		if (!authService.isAuthenticated()) {
			throw new Exception("Not Authenticated");
		}

		HttpResponse response;

		try {
			response = httpClient.get(SubPaths.API.toString()
					+ Endpoints.USER_COURSES.toString().replace(":user_id", authService.getCurrentUserId().asHex())
					+ "?limit=1");
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return -1;
		}

		if (response.getStatusLine().getStatusCode() / 100 == 2) {
			String responseBody = BasicHttpClient.getResponseBody(response);
			JSONObject responseJson = (JSONObject) new JSONParser().parse(responseBody);

			if (responseJson.containsKey("pagination")) {
				JSONObject paginationJson = (JSONObject) responseJson.get("pagination");

				if (paginationJson.containsKey("total")) {
					return Integer.parseInt(paginationJson.get("total").toString());
				}
			}
		}

		return -1;
	}

}
