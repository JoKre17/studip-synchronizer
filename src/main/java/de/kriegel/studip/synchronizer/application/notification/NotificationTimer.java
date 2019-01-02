package de.kriegel.studip.synchronizer.application.notification;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		// Map<Course, List<CourseNews>> courseNewsMap =
		// notificationManager.getAllNewCoursNews();

		this.setChanged();
		this.notifyObservers();

	}

}

class NotificationTimerThread extends Thread {

	private static final Logger log = LoggerFactory.getLogger(NotificationTimerThread.class);

	// 10 seconds
	private final long TIMER_STARTUP_MILLIS = 10000;

	// 30 seconds
	// private final long TIMER_STARTUP_MILLIS = 30 * 1000;

	// 5 minutes
	private final long TIMER_INTERVAL_MILLIS = 5 * 60 * 1000;

	private NotificationTimer notificationTimer;

	public NotificationTimerThread(NotificationTimer notificationTimer) {
		this.notificationTimer = notificationTimer;

		this.setDaemon(true);
	}

	@Override
	public void run() {

		try {
			log.info("Waiting " + (TIMER_STARTUP_MILLIS / 1000.0) + " seconds before first run.");
			Thread.sleep(TIMER_STARTUP_MILLIS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			return;
		}

		while (true) {
			log.debug("Notification Timer: Trigger!");
			notificationTimer.trigger();

			try {
				log.info("Waiting " + (TIMER_INTERVAL_MILLIS / (60.0 * 1000.0)) + " minutes before next run.");
				Thread.sleep(TIMER_INTERVAL_MILLIS);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				return;
			}
		}

	}
}
