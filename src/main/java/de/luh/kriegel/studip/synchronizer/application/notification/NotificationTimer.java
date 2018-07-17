package de.luh.kriegel.studip.synchronizer.application.notification;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;

public class NotificationTimer extends Observable {

	private NotificationTimerThread thread;
	private NotificationManager notificationManager;

	public NotificationTimer(NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
	}

	public void start() {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}

		thread = new NotificationTimerThread(this);
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}

	protected void trigger() {

		try {
			Map<Course, List<CourseNews>> courseNewsMap = notificationManager.getAllNewCoursNews();

			this.setChanged();
			this.notifyObservers(courseNewsMap);

		} catch (NotAuthenticatedException | ParseException e) {
			e.printStackTrace();
		}

	}

}

class NotificationTimerThread extends Thread {

	// 3 seconds
	private final long TIMER_STARTUP_MILLIS = 3000;

	// 30 seconds
	// private final long TIMER_STARTUP_MILLIS = 30 * 1000;

	// 5 minutes
	private final long TIMER_INTERVAL_MILLIS = 5 * 60 * 1000;

	private NotificationTimer notificationTimer;

	public NotificationTimerThread(NotificationTimer notificationTimer) {
		this.notificationTimer = notificationTimer;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(TIMER_STARTUP_MILLIS);
		} catch (InterruptedException e) {
			return;
		}

		while (true) {
			notificationTimer.trigger();

			try {
				Thread.sleep(TIMER_INTERVAL_MILLIS);
			} catch (InterruptedException e) {
				return;
			}
		}

	}
}
