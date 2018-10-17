package de.luh.kriegel.studip.synchronizer.event;

import java.io.File;
import java.util.List;

import de.luh.kriegel.studip.synchronizer.content.model.data.Course;

public class CourseDownloadFinishedEvent {

	private Course course;
	private List<File> downloadedFiles;

	public CourseDownloadFinishedEvent(Course course, List<File> downloadedFiles) {
		this.course = course;
		this.downloadedFiles = downloadedFiles;
	}

	public Course getCourse() {
		return course;
	}

	public List<File> getDownloadedFiles() {
		return downloadedFiles;
	}

	@Override
	public String toString() {
		return course.getTitleAsValidFilename() + " Downloaded files: " + downloadedFiles.size();
	}
}
