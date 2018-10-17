package de.luh.kriegel.studip.synchronizer.application.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventManager {

	private static final Logger log = LogManager.getLogger(EventManager.class);

	private final File allEventsJSONLogfile;
	private final String ALL_EVENTS_LOGFILE = "allEventsLog.json";
	private final List<CourseNewsReceivedEvent> oldCourseNewsReceivedEvents;

	public EventManager() throws Exception {
		allEventsJSONLogfile = new File(ALL_EVENTS_LOGFILE);
		if (!allEventsJSONLogfile.exists()) {
			boolean successfullyCreatedLogfile = allEventsJSONLogfile.createNewFile();

			if (!successfullyCreatedLogfile) {
				throw new Exception("Could not create file " + allEventsJSONLogfile.getAbsolutePath());
			}

			oldCourseNewsReceivedEvents = new ArrayList<>();
		} else {
			if (!allEventsJSONLogfile.canWrite() || !allEventsJSONLogfile.canRead()) {
				throw new Exception("Could not read or write from file " + allEventsJSONLogfile.getAbsolutePath());
			}

			oldCourseNewsReceivedEvents = loadCourseEventsFromJSONFile(allEventsJSONLogfile);
		}
	}

	private List<CourseNewsReceivedEvent> loadCourseEventsFromJSONFile(File allEventsJSONLogfile2) {
		// TODO Auto-generated method stub
		return null;
	}

}
