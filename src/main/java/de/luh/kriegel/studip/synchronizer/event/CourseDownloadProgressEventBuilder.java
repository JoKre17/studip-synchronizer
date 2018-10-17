package de.luh.kriegel.studip.synchronizer.event;

import java.util.Date;

import org.json.simple.JSONObject;

import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.client.service.CourseService;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.Id;

public class CourseDownloadProgressEventBuilder extends EventBuilder {
	
	private final CourseService courseService;
	
	public CourseDownloadProgressEventBuilder(CourseService courseService) {
		assert courseService != null;
		
		this.courseService = courseService;
	};
	
	@Override
	public CourseDownloadProgressEvent fromJson(JSONObject json) throws NotAuthenticatedException {
		
		Id courseId = new Id((String) json.get("courseId"));
		Course course = courseService.getCourseById(courseId);
		
		double progress = Double.parseDouble((String) json.get("progress"));

		Date eventDate = new Date(Long.parseLong((String) json.get("eventDate")));
		
		return new CourseDownloadProgressEvent(course, progress, eventDate);
	}
	
}