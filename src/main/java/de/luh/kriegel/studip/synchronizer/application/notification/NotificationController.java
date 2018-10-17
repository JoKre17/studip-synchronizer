package de.luh.kriegel.studip.synchronizer.application.notification;

import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import de.luh.kriegel.studip.synchronizer.application.event.CourseNewsReceivedEvent;
import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;
import de.luh.kriegel.studip.synchronizer.event.Event;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.util.Duration;

public class NotificationController implements Observer {

	private final Logger log = LogManager.getLogger(NotificationController.class);

	private final TrayIcon trayIcon;
	private final NotificationManager notificationManager;
	private final NotificationTimer notificationTimer;

	private final ReentrantLock lock = new ReentrantLock();
	private final ObservableList<NotificationView> notificationViews = FXCollections.observableArrayList();
	private final ObservableList<NotificationView> notificationQueue = FXCollections.observableArrayList();
	private final int NOTIFICATION_DISPLAY_AMOUNT_MAX;

	private final List<NotificationViewCreatedEventListener> notificationViewCreatedEventListeners = new ArrayList<>();

	public NotificationController() throws Exception {
		trayIcon = SynchronizerApp.simpleWindowStage.getController().trayIcon;
		notificationManager = new NotificationManager();
		notificationTimer = new NotificationTimer(notificationManager);
		notificationTimer.addObserver(this);

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

		notificationViews.addListener(new ListChangeListener<NotificationView>() {

			@Override
			public void onChanged(Change<? extends NotificationView> arg0) {

				int TRANSITION_DURATION_MILLIS = 800;

				Rectangle2D screenResolutionRect = Screen.getPrimary().getBounds();

				lock.lock();
				while (arg0.next()) {
					if (arg0.getAddedSize() > 0) {
						for (NotificationView notificationView : arg0.getAddedSubList()) {
							FadeTransition fadeInTransition = new FadeTransition(
									Duration.millis(TRANSITION_DURATION_MILLIS), notificationView.getScene().getRoot());
							fadeInTransition.setFromValue(0.0);
							fadeInTransition.setToValue(1.0);

							TranslateTransition translateTransition = new TranslateTransition(
									Duration.millis(TRANSITION_DURATION_MILLIS), notificationView.getScene().getRoot());
							translateTransition.setByX(-notificationView.getWidth());

							FadeTransition fadeOutTransition = new FadeTransition(
									Duration.millis(TRANSITION_DURATION_MILLIS), notificationView.getScene().getRoot());
							fadeOutTransition.setFromValue(1.0);
							fadeOutTransition.setToValue(0.0);
							fadeOutTransition.setDelay(Duration.millis(8000));
							fadeOutTransition.setOnFinished((ae) -> {
								notificationViews.remove(notificationView);
							});

							if (arg0.getList().size() > 1) {
								fadeInTransition.setDelay(Duration.millis(TRANSITION_DURATION_MILLIS));
								translateTransition.setDelay(Duration.millis(TRANSITION_DURATION_MILLIS));
							}

							Platform.runLater(() -> {
								notificationView
										.setX(screenResolutionRect.getMaxX() - notificationView.getWidth() - 10);
								notificationView.getScene().getRoot().setTranslateX(notificationView.getWidth());
								notificationView
										.setY(screenResolutionRect.getMaxY() - notificationView.getHeight() - 50);

								fadeInTransition.play();
								translateTransition.play();
								fadeOutTransition.play();
							});
						}

						for (NotificationView notificationView : arg0.getList()) {
							if (!arg0.getAddedSubList().contains(notificationView)) {

								double startPos = notificationView.getY();
								int index = arg0.getList().size() - arg0.getList().indexOf(notificationView);
								double endPos = screenResolutionRect.getMaxY() - 40
										- (notificationView.getHeight() + 10) * index;
								DoubleProperty y = new SimpleDoubleProperty(startPos);
								Timeline timeline = new Timeline(new KeyFrame(
										Duration.millis(TRANSITION_DURATION_MILLIS), new KeyValue(y, endPos)));
								y.addListener(
										(obs, oldValue, newValue) -> notificationView.setY(newValue.doubleValue()));

								Platform.runLater(() -> {
									timeline.play();
								});

								// TranslateTransition translateTransition = new TranslateTransition(
								// Duration.millis(TRANSITION_DURATION_MILLIS),
								// notificationView.getScene().getRoot());
								// translateTransition.setByY(-notificationView.getHeight() - 10);
								// translateTransition.play();
							}
						}
					}
				}
				lock.unlock();

			}

		});

		NOTIFICATION_DISPLAY_AMOUNT_MAX = (int) ((Screen.getPrimary().getBounds().getMaxY() - 50)
				/ (NotificationView.HEIGHT + 10));
		log.debug("Max amount notifications: " + NOTIFICATION_DISPLAY_AMOUNT_MAX);

		notificationQueue.addListener(new ListChangeListener<NotificationView>() {

			Thread processNotificationQueueThread;

			@Override
			public void onChanged(Change<? extends NotificationView> arg0) {
				while (arg0.next()) {
					if (arg0.getAddedSize() > 0
							&& (processNotificationQueueThread == null || !processNotificationQueueThread.isAlive())) {
						processNotificationQueueThread = new Thread(new Runnable() {

							@Override
							public void run() {
								while (!notificationQueue.isEmpty()) {
									while (!notificationQueue.isEmpty()
											&& notificationViews.size() < NOTIFICATION_DISPLAY_AMOUNT_MAX) {
										NotificationView notificationView = notificationQueue
												.get(notificationQueue.size() - 1);
										notificationQueue.remove(notificationQueue.size() - 1);

										lock.lock();
										notificationViews.add(notificationViews.size(), notificationView);
										lock.unlock();
										try {
											Thread.sleep(200);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									try {
										Thread.sleep(50);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}

						});

						processNotificationQueueThread.setDaemon(true);
						processNotificationQueueThread.start();
					}
				}
			}

		});

	}

	public void addNotificationViewCreatedEventListener(
			NotificationViewCreatedEventListener notificationViewCreatedEventListener) {
		notificationViewCreatedEventListeners.add(notificationViewCreatedEventListener);
	}

	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	public void loginPerformed() {
		if (ConfigManager.getNotificationsEnabledProperty().get()) {
			notificationTimer.start();
		}

		// TEST //
		String title = "Achtung: Ihre Teilnahme ist verbindlich!";

		String content = "<!--HTML-->\r\n" + "\r\n"
				+ "<p>Achtung: Ihre Teilnahme ist verbindlich! Sollten Sie an einem Seminar nicht teilnehmen können, dann melden Sie sich bitte umgehend - spätestens jedoch 2 Wochen vor Seminarbeginn - ab, indem Sie sich aus der Veranstaltung in Stud.IP austragen. Bitte tragen Sie sich auch aus Wartelisten aus, wenn Sie wissen, dass Sie nicht teilnehmen werden. Sie blockieren sonst einen Seminarplatz, den eine andere Person wahrnehmen könnte!</p>\r\n"
				+ "\r\n"
				+ "<p>Lesen Sie hierzu auch die Teilnahmebedingungen auf unserer Homepage unter <a href=\"https://www.sk.uni-hannover.de/faq_infos.html\" class=\"link-extern\">https://www.sk.uni-hannover.de/faq_infos.html</a></p>";

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					displayMessage("Notification 1", content, NotificationType.COURSE_NEWS,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 2", content, NotificationType.DOWNLOAD,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 3", content, NotificationType.COURSE_NEWS,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 4", content, NotificationType.DOWNLOAD,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 5", content, NotificationType.COURSE_NEWS,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 6", content, NotificationType.DOWNLOAD,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 7", content, NotificationType.COURSE_NEWS,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 8", content, NotificationType.DOWNLOAD,
							new CourseNewsReceivedEvent(null));
					Thread.sleep(200);
					displayMessage("Notification 9", content, NotificationType.COURSE_NEWS,
							new CourseNewsReceivedEvent(null));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});
		// .start();

	}

	public void displayMessage(String title, String content, NotificationType notificationType, Event event) {
		assert title != null;
		assert content != null;

		new Thread(new Runnable() {

			NotificationView notificationView;

			@Override
			public void run() {
				Platform.runLater(() -> {
					notificationView = new NotificationView(title, content, notificationType, event);
					notificationView.getScene().getRoot().setOpacity(0);
					notificationView.show();

					for (NotificationViewCreatedEventListener listener : notificationViewCreatedEventListeners) {
						listener.onNotificationViewCreated(notificationView);
					}

					notificationQueue.add(0, notificationView);
				});
			}
		}).start();

		// trayIcon.displayMessage(title, content, MessageType.INFO);

	}

	@Override
	public void update(Observable o, Object arg) {

		try {
			Map<Course, List<CourseNews>> courseNewsMap = notificationManager.getAllNewCoursNews();

			for (Course c : courseNewsMap.keySet()) {
				List<CourseNews> courseNewsForCourse = courseNewsMap.get(c);

				for (CourseNews courseNews : courseNewsForCourse) {
					displayMessage(courseNews.getTopic(), courseNews.getBody(), NotificationType.COURSE_NEWS,
							new CourseNewsReceivedEvent(courseNews));
				}
			}
		} catch (NotAuthenticatedException e) {
			log.error(e.getMessage(), e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}

}
