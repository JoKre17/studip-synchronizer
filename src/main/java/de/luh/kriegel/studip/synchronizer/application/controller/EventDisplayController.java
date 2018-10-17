package de.luh.kriegel.studip.synchronizer.application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.event.CourseNewsReceivedEvent;
import de.luh.kriegel.studip.synchronizer.application.event.CourseNewsReceivedEventListener;
import de.luh.kriegel.studip.synchronizer.application.event.EventManager;
import de.luh.kriegel.studip.synchronizer.event.CourseDownloadFinishedEvent;
import de.luh.kriegel.studip.synchronizer.event.CourseDownloadFinishedEventListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;

public class EventDisplayController implements Initializable {

	private static final Logger log = LogManager.getLogger(EventDisplayController.class);

	@FXML
	private JFXTabPane root;

	@FXML
	private Tab allEventsTab;

	@FXML
	private Tab downloadEventsTab;

	@FXML
	private Tab courseNewsEventsTab;

	@FXML
	private JFXListView allEventsJFXListView;

	@FXML
	private JFXListView downloadEventsJFXListView;

	@FXML
	private JFXListView courseNewsEventsJFXListView;

	private EventManager eventManager;

	private final CourseDownloadFinishedEventListener courseDownloadFinishedEventListener;
	private final CourseNewsReceivedEventListener courseNewsReceivedEventListener;

	public EventDisplayController() {

		try {
			eventManager = new EventManager();
		} catch (Exception e) {
			e.printStackTrace();
		}

		courseDownloadFinishedEventListener = new CourseDownloadFinishedEventListener() {
			@Override
			public void onCourseDownloadFinished(CourseDownloadFinishedEvent courseDownloadFinishedEvent) {
				log.info("Received " + courseDownloadFinishedEvent.getClass().getSimpleName());
			}
		};

		courseNewsReceivedEventListener = new CourseNewsReceivedEventListener() {
			@Override
			public void onCourseNewsReceived(CourseNewsReceivedEvent courseNewsReceivedEvent) {
				log.info("Received " + courseNewsReceivedEvent.getClass().getSimpleName());
			}
		};

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		log.debug("Initialize EventDisplayController");

		SynchronizerApp.studipClient.getCourseService().getDownloadManager()
				.addCourseDownloadFinishedEventListener(courseDownloadFinishedEventListener);
		SynchronizerApp.notificationController.getNotificationManager()
				.addCourseNewsReceivedEventListener(courseNewsReceivedEventListener);
	}

}
