package de.luh.kriegel.studip.synchronizer.application.event;

import org.json.simple.JSONObject;

import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.client.service.CourseService;
import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;
import de.luh.kriegel.studip.synchronizer.content.model.data.Id;
import de.luh.kriegel.studip.synchronizer.event.EventBuilder;

public class CourseNewsReceivedEventBuilder extends EventBuilder {
	
	private CourseService courseService;
	
	public CourseNewsReceivedEventBuilder(CourseService courseService) {
		this.courseService = courseService;
	}
	
	@Override
	public CourseNewsReceivedEvent fromJson(JSONObject json) throws NotAuthenticatedException {
		
		Id courseNewsId = new Id((String) json.get("courseNewsId"));
		
		CourseNews courseNews = courseService.getCourseNewsForCourseNewsId(courseNewsId);
		
		return new CourseNewsReceivedEvent(courseNews);
	}
	
}