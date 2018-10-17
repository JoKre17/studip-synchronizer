package de.luh.kriegel.studip.synchronizer.application.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.client.service.CourseService;
import de.luh.kriegel.studip.synchronizer.event.CourseDownloadFinishedEvent;
import de.luh.kriegel.studip.synchronizer.event.CourseDownloadFinishedEventBuilder;
import de.luh.kriegel.studip.synchronizer.event.CourseDownloadFinishedEventListener;
import de.luh.kriegel.studip.synchronizer.event.CourseDownloadProgressEventBuilder;
import de.luh.kriegel.studip.synchronizer.event.Event;
import de.luh.kriegel.studip.synchronizer.event.EventBuilder;
import de.luh.kriegel.studip.synchronizer.event.EventType;

public class EventManager {

	private static final Logger log = LogManager.getLogger(EventManager.class);

	private final File allEventsJSONLogfile;
	private final String ALL_EVENTS_LOGFILE = "allEventsLog.json";
	private final List<Event> allLoggedEvents;

	private final CourseDownloadFinishedEventListener courseDownloadFinishedEventListener;
	private final CourseNewsReceivedEventListener courseNewsReceivedEventListener;

	public EventManager() throws Exception {
		allEventsJSONLogfile = new File(ALL_EVENTS_LOGFILE);
		if (!allEventsJSONLogfile.exists()) {
			boolean successfullyCreatedLogfile = allEventsJSONLogfile.createNewFile();

			if (!successfullyCreatedLogfile) {
				throw new Exception("Could not create file " + allEventsJSONLogfile.getAbsolutePath());
			}

			allLoggedEvents = new ArrayList<>();
		} else {
			if (!allEventsJSONLogfile.canWrite() || !allEventsJSONLogfile.canRead()) {
				throw new Exception("Could not read or write from file " + allEventsJSONLogfile.getAbsolutePath());
			}

			allLoggedEvents = loadEventsFromJSONFile(allEventsJSONLogfile);
		}

		courseDownloadFinishedEventListener = new CourseDownloadFinishedEventListener() {
			@Override
			public void onCourseDownloadFinished(CourseDownloadFinishedEvent courseDownloadFinishedEvent) {
				log.info("course download finished event");
				allLoggedEvents.add(courseDownloadFinishedEvent);
				try {
					saveEventsToJSONFile(allEventsJSONLogfile);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		};

		courseNewsReceivedEventListener = new CourseNewsReceivedEventListener() {
			@Override
			public void onCourseNewsReceived(CourseNewsReceivedEvent courseNewsReceivedEvent) {
				log.info("course news received event");
				allLoggedEvents.add(courseNewsReceivedEvent);
				try {
					saveEventsToJSONFile(allEventsJSONLogfile);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		};

		SynchronizerApp.studipClient.getCourseService().getDownloadManager()
				.addCourseDownloadFinishedEventListener(courseDownloadFinishedEventListener);
		SynchronizerApp.notificationController.getNotificationManager()
				.addCourseNewsReceivedEventListener(courseNewsReceivedEventListener);
	}

	private List<Event> loadEventsFromJSONFile(File allEventsJSONLogfile) throws ParseException {
		log.debug("Loading old events from file");
		List<Event> oldEvents = new ArrayList<>();

		JSONParser jsonParser = new JSONParser();

		try (BufferedReader br = new BufferedReader(new FileReader(allEventsJSONLogfile))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.trim().equals("")) {
					continue;
				}

				JSONObject json = (JSONObject) jsonParser.parse(line.trim());

				EventType eventType = EventType.valueOf((String) json.get("eventType"));
				Date eventDate = new Date((long) json.get("date"));

				JSONObject eventJson = (JSONObject) jsonParser.parse((String) json.get("data"));
				Event event;
				try {
					event = parseJsonToEvent(eventType, eventJson);
					oldEvents.add(event);
				} catch (NotAuthenticatedException e) {
					e.printStackTrace();
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return oldEvents;
	}

	private Event parseJsonToEvent(EventType eventType, JSONObject eventJson) throws NotAuthenticatedException {

		CourseService courseService = SynchronizerApp.studipClient.getCourseService();

		EventBuilder eventBuilder = null;

		switch (eventType) {
		case DOWNLOAD_COMPLETED:
			eventBuilder = new CourseDownloadFinishedEventBuilder(courseService);
			break;
		case COURSE_NEWS_RECEIVED:
			eventBuilder = new CourseNewsReceivedEventBuilder(courseService);
			break;
		case COURSE_DOWNLOAD_PROGRESS:
			eventBuilder = new CourseDownloadProgressEventBuilder(courseService);
			break;
		default:
			break;
		}

		if (eventBuilder != null) {
			return eventBuilder.fromJson(eventJson);
		}

		return null;

	}

	@SuppressWarnings({ "unchecked" })
	private void saveEventsToJSONFile(File allEventsJSONLogfile)
			throws FileNotFoundException, UnsupportedEncodingException {
		log.debug("Saving old events to file");

		PrintWriter writer = new PrintWriter(allEventsJSONLogfile, "UTF-8");

		for (Event event : allLoggedEvents) {
			JSONObject eventIdentifierJson = event.toJsonIdentifier();
			JSONObject eventJson = event.toJson();

			eventIdentifierJson.put("data", eventJson.toJSONString());

			writer.println(eventIdentifierJson.toJSONString());
		}

		writer.close();
	}

	public List<Event> getAllLoggedEvents() {
		return allLoggedEvents;
	}

}
