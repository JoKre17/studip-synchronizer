package de.luh.kriegel.studip.synchronizer.application.notification;

import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.CourseNews;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class NotificationController implements Observer {

	private final Logger log = LogManager.getLogger(NotificationController.class);

	private final TrayIcon trayIcon;
	private final NotificationManager notificationManager;
	private final NotificationTimer notificationTimer;

	private final List<NotificationView> notificationViews = new ArrayList<>();
	
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
		
		String title = "Achtung: Ihre Teilnahme ist verbindlich!";
		
		String content = "<!--HTML-->\r\n" + 
				"\r\n" + 
				"<p>Achtung: Ihre Teilnahme ist verbindlich! Sollten Sie an einem Seminar nicht teilnehmen können, dann melden Sie sich bitte umgehend - spätestens jedoch 2 Wochen vor Seminarbeginn - ab, indem Sie sich aus der Veranstaltung in Stud.IP austragen. Bitte tragen Sie sich auch aus Wartelisten aus, wenn Sie wissen, dass Sie nicht teilnehmen werden. Sie blockieren sonst einen Seminarplatz, den eine andere Person wahrnehmen könnte!</p>\r\n" + 
				"\r\n" + 
				"<p>Lesen Sie hierzu auch die Teilnahmebedingungen auf unserer Homepage unter <a href=\"https://www.sk.uni-hannover.de/faq_infos.html\" class=\"link-extern\">https://www.sk.uni-hannover.de/faq_infos.html</a></p>";

		displayMessage(title, content, NotificationType.COURSE_NEWS);
		
	}

	public void displayMessage(String title, String content, NotificationType notificationType) {
		assert title != null;
		assert content != null;
		
		Rectangle2D rect = Screen.getPrimary().getBounds();
		
		Platform.runLater(() -> {
			NotificationView notificationView = new NotificationView(title, content, notificationType);
			
			notificationView.show();
			notificationView.setX(rect.getMaxX() - notificationView.getWidth() - 10);
			notificationView.setY(rect.getMaxY() - notificationView.getHeight() - 50);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						Platform.runLater(() -> {
							notificationView.hide();
						});
					}
				}
			}).start();
			
			
		});
		
		

//		trayIcon.displayMessage(title, content, MessageType.INFO);

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

