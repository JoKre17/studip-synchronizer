package de.luh.kriegel.studip.synchronizer.application.event;

import de.luh.kriegel.studip.synchronizer.event.CourseDownloadFinishedEvent;

public enum EventType {
	DOWNLOAD_COMPLETED, COURSE_NEWS_RECEIVED, DEFAULT;

	public EventType getEventTypeForEvent(Object o) {

		if (o instanceof CourseDownloadFinishedEvent) {
			return DOWNLOAD_COMPLETED;
		}

		if (o instanceof CourseNewsReceivedEvent) {
			return COURSE_NEWS_RECEIVED;
		}

		return DEFAULT;
	}
}
