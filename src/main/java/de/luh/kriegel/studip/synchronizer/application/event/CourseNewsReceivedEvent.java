package de.luh.kriegel.studip.synchronizer.application.event;

import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;

public class CourseNewsReceivedEvent {

	private final CourseNews courseNews;

	public CourseNewsReceivedEvent(CourseNews courseNews) {
		this.courseNews = courseNews;
	}

	public CourseNews get() {
		return courseNews;
	}

}
