package de.luh.kriegel.studip.synchronizer.application.notification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.event.CourseNewsReceivedEvent;
import de.luh.kriegel.studip.synchronizer.application.event.CourseNewsReceivedEventListener;
import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;
import de.luh.kriegel.studip.synchronizer.content.model.data.Id;

public class NotificationManager {

	private final Logger log = LogManager.getLogger(NotificationManager.class);

	private final List<CourseNewsReceivedEventListener> courseNewsReceivedEventListeners = new ArrayList<>();

	private final File courseNewsIdLogfile;
	private final String COURSE_NEWS_ID_LOGFILE = "courseNewsIds.log";
	private final List<Id> oldCourseNewsIds;

	public NotificationManager() throws Exception {
		courseNewsIdLogfile = new File(COURSE_NEWS_ID_LOGFILE);
		if (!courseNewsIdLogfile.exists()) {
			boolean successfullyCreatedLogfile = courseNewsIdLogfile.createNewFile();

			if (!successfullyCreatedLogfile) {
				throw new Exception("Could not create file " + courseNewsIdLogfile.getAbsolutePath());
			}

			oldCourseNewsIds = new ArrayList<>();
		} else {
			if (!courseNewsIdLogfile.canWrite() || !courseNewsIdLogfile.canRead()) {
				throw new Exception("Could not read or write from file " + courseNewsIdLogfile.getAbsolutePath());
			}

			oldCourseNewsIds = loadCourseNewsIdsFromFile(courseNewsIdLogfile);
		}
	}

	public void addCourseNewsReceivedEventListener(CourseNewsReceivedEventListener courseNewsReceivedEventListener) {
		this.courseNewsReceivedEventListeners.add(courseNewsReceivedEventListener);
	}

	public void removeCourseNewsReceivedEventListener(CourseNewsReceivedEventListener courseNewsReceivedEventListener) {
		this.courseNewsReceivedEventListeners.remove(courseNewsReceivedEventListener);
	}

	public List<CourseNewsReceivedEventListener> getCourseNewsReceivedEventListeners() {
		return courseNewsReceivedEventListeners;
	}

	private List<Id> loadCourseNewsIdsFromFile(File logfile) {
		log.debug("Loading old courseNews ids from file");
		List<Id> oldCourseNewsIds = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(logfile))) {
			for (String line; (line = br.readLine()) != null;) {
				if (line.trim().isEmpty()) {
					continue;
				}
				Id id = new Id(line.trim());
				oldCourseNewsIds.add(id);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return oldCourseNewsIds;
	}

	private void saveOldCourseNewsIdsToLogfile(File logfile)
			throws FileNotFoundException, UnsupportedEncodingException {
		log.debug("Saving old courseNews ids to file");

		PrintWriter writer = new PrintWriter(logfile, "UTF-8");

		for (Id id : oldCourseNewsIds) {
			writer.println(id.toString());
		}

		writer.close();
	}

	public List<Id> getAllOldCourseNewsIds() {
		return oldCourseNewsIds;
	}

	public Map<Course, List<CourseNews>> getAllNewCoursNews() throws NotAuthenticatedException, ParseException {

		log.info("Fetch all new course news");

		Map<Course, List<CourseNews>> courseNewsMap = new HashMap<>();
		List<Course> allCourses = SynchronizerApp.studipClient.getCourseService().getAllCourses();

		for (Course c : allCourses) {
			List<CourseNews> courseNews = SynchronizerApp.studipClient.getCourseService()
					.getAllCourseNewsForCourseId(c.getId());

			List<CourseNews> newCourseNews = new ArrayList<>();
			for (CourseNews cn : courseNews) {
				if (!oldCourseNewsIds.contains(cn.getId())) {
					for (CourseNewsReceivedEventListener eventListener : courseNewsReceivedEventListeners) {
						eventListener.onCourseNewsReceived(new CourseNewsReceivedEvent(cn));
					}
					newCourseNews.add(cn);
					oldCourseNewsIds.add(cn.getId());
				}
			}

			if (!newCourseNews.isEmpty()) {
				courseNewsMap.put(c, newCourseNews);
			}
		}

		if (!courseNewsMap.isEmpty()) {
			try {
				saveOldCourseNewsIdsToLogfile(courseNewsIdLogfile);
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		log.info("Fetching course news finished.");
		log.info("Fetched new news for " + courseNewsMap.size() + " courses.");
		return courseNewsMap;
	}

}
