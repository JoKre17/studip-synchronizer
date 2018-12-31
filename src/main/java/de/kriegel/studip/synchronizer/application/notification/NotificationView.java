package de.kriegel.studip.synchronizer.application.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kriegel.studip.client.event.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NotificationView extends Stage {
	
	public static final String HTML_STYLE = "<style>body{" + "overflow: hidden;\n" + "font-family: Lato,sans-serif;\n"
			+ "font-size: 14px;" + "line-height: 1.42857143;" + "color: #000;" + "background-color: #f5f5f6;"
			+ "}</style>";

	private static final Logger log = LogManager.getLogger(NotificationView.class);

	public static final int HEIGHT = 200;

	@FXML
	private Label title;

	@FXML
	private WebView webView;

	@FXML
	private HBox titleBar;

	@FXML
	private VBox notificationTypeColorIndicationPanel;

	private String notificationTitle;
	private Event event;
	
	private List<NotificationViewClickedEventListener> notificationViewClickedEventListeners = new ArrayList<>();
	private List<NotificationViewClosedEventListener> notificationViewClosedEventListeners = new ArrayList<>();
	
	public NotificationView(String title, String content, NotificationType notificationType, Event event) {
		super(StageStyle.TRANSPARENT);

		notificationTitle = title;
		this.event = event;

		Stage wrapperStage = new Stage(StageStyle.UTILITY);
		wrapperStage.setOpacity(0);
		wrapperStage.setHeight(0);
		wrapperStage.setWidth(0);
		wrapperStage.show();

		initOwner(wrapperStage);

		wrapperStage.setAlwaysOnTop(true);

		if (notificationType == null) {
			notificationType = NotificationType.DEFAULT;
		}

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/NotificationPane.fxml"));
		loader.setController(this);

		Region root;

		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Scene scene = new Scene(root);
		scene.setFill(Color.TRANSPARENT);
		setScene(scene);

		setNotificationTypeIdentificatorPanelBackgroundColor(notificationType);

		Platform.runLater(() -> {
			this.title.setText(title);
			WebEngine webEngine = webView.getEngine();

			webEngine.loadContent(HTML_STYLE + content);
		});

	}
	
	public void addNotificationViewClickedEventListener(NotificationViewClickedEventListener notificationViewClickedEventListener) {
		this.notificationViewClickedEventListeners.add(notificationViewClickedEventListener);
	}

	public void removeNotificationViewClickedEventListener(NotificationViewClickedEventListener notificationViewClickedEventListener) {
		this.notificationViewClickedEventListeners.remove(notificationViewClickedEventListener);
	}

	@FXML
	private void onMouseClicked(MouseEvent e) {
		for(NotificationViewClickedEventListener listener : notificationViewClickedEventListeners) {
			listener.onNotificationViewClicked(this);
		}
		
		notificationViewClickedEventListeners.clear();
		this.hide();
	}

	@FXML
	private void closeButtonOnMouseClicked(MouseEvent e) {
		for(NotificationViewClosedEventListener listener : notificationViewClosedEventListeners) {
			listener.onNotificationViewClosed(this);
		}
		
		notificationViewClosedEventListeners.clear();
		this.hide();
	}

	private void setNotificationTypeIdentificatorPanelBackgroundColor(NotificationType notificationType) {

		String style = notificationTypeColorIndicationPanel.getStyle();

		switch (notificationType) {
		case DOWNLOAD:
			style += "-fx-background-color: derive(green, 0.8);";
			break;
		case COURSE_NEWS:
			style += "-fx-background-color: derive(red, 0.8);";
			break;
		case DEFAULT:

			break;
		}

		notificationTypeColorIndicationPanel.setStyle(style);
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public interface NotificationViewClickedEventListener {
		public void onNotificationViewClicked(NotificationView notificationView);
	}

	public interface NotificationViewClosedEventListener {
		public void onNotificationViewClosed(NotificationView notificationView);
	}
	

}
