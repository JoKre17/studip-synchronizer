package de.luh.kriegel.studip.synchronizer.event;

import de.luh.kriegel.studip.synchronizer.content.model.data.Course;

public class CourseDownloadProgressEvent {

	private Course course;
	private double progress;
	
	public CourseDownloadProgressEvent(Course course, double progress) {
		this.course = course;
		this.progress = progress;
	}

	public Course getCourse() {
		return course;
	}

	public double getProgress() {
		return progress;
	}
	
	public double getProgressInPercent() {
		return Math.round(progress * 100.0) / 100.0;
	}

	@Override
	public String toString() {
		return course.getTitleAsValidFilename() + " Progress: " + Math.round(progress * 100.0) / 100.0;
	}
	
}
