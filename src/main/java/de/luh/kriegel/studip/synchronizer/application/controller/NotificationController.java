package de.luh.kriegel.studip.synchronizer.application.controller;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationManager;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationTimer;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class NotificationController implements Observer {

	private final Logger log = LogManager.getLogger(NotificationController.class);

	private final TrayIcon trayIcon;
	private final NotificationManager notificationManager;
	private final NotificationTimer notificationTimer;

	public NotificationController() throws Exception {
		trayIcon = SynchronizerApp.simpleWindowStage.getController().trayIcon;
		notificationManager = new NotificationManager();
		notificationTimer = new NotificationTimer(notificationManager);
		notificationTimer.addObserver(this);

		if (ConfigManager.getNotificationsEnabledProperty().get()) {
			notificationTimer.start();
		}

		ConfigManager.getNotificationsEnabledProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					notificationTimer.start();
				} else {
					notificationTimer.stop();
				}
			}
		});

	}

	public void displayMessage(String title, String content, NotificationType notificationType) {
		assert title != null;
		assert content != null;

		if (notificationType == null) {
			notificationType = NotificationType.DEFAULT;
		}

		trayIcon.displayMessage(title, content, MessageType.INFO);

	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<Course, List<CourseNews>> courseNewsMap = (Map<Course, List<CourseNews>>) arg;

			for (Course c : courseNewsMap.keySet()) {
				List<CourseNews> courseNewsForCourse = courseNewsMap.get(c);

				for (CourseNews courseNews : courseNewsForCourse) {
					displayMessage(courseNews.getTopic(), courseNews.getBody(), NotificationType.COURSE_NEWS);
				}
			}

		}

	}

}

enum NotificationType {
	DOWNLOAD, COURSE_NEWS, DEFAULT
}
