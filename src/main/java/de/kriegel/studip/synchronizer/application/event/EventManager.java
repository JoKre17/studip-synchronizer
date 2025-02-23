package de.kriegel.studip.synchronizer.application.event;

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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kriegel.studip.client.event.CourseDownloadFinishedEvent;
import de.kriegel.studip.client.event.CourseDownloadFinishedEventBuilder;
import de.kriegel.studip.client.event.CourseDownloadFinishedEventListener;
import de.kriegel.studip.client.event.CourseDownloadProgressEventBuilder;
import de.kriegel.studip.client.event.CourseNewsReceivedEvent;
import de.kriegel.studip.client.event.CourseNewsReceivedEventBuilder;
import de.kriegel.studip.client.event.CourseNewsReceivedEventListener;
import de.kriegel.studip.client.event.Event;
import de.kriegel.studip.client.event.EventBuilder;
import de.kriegel.studip.client.event.EventType;
import de.kriegel.studip.client.exception.NotAuthenticatedException;
import de.kriegel.studip.client.service.CourseService;
import de.kriegel.studip.synchronizer.application.SynchronizerApp;

public class EventManager {

	private static final Logger log = LoggerFactory.getLogger(EventManager.class);

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
				log.debug("course download finished event");
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
