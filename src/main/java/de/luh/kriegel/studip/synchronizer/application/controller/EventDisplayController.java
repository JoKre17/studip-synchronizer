package de.luh.kriegel.studip.synchronizer.application.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.client.event.CourseDownloadFinishedEvent;
import de.luh.kriegel.studip.client.event.CourseDownloadFinishedEventListener;
import de.luh.kriegel.studip.client.event.CourseDownloadProgressEvent;
import de.luh.kriegel.studip.client.event.CourseNewsReceivedEvent;
import de.luh.kriegel.studip.client.event.CourseNewsReceivedEventListener;
import de.luh.kriegel.studip.client.event.Event;
import de.luh.kriegel.studip.client.event.EventView;
import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.event.CourseDownloadFinishedEventView;
import de.luh.kriegel.studip.synchronizer.application.event.EventListCell;
import de.luh.kriegel.studip.synchronizer.application.event.EventManager;
import de.luh.kriegel.studip.synchronizer.application.event.HtmlEventEntryView;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationView;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationView.NotificationViewClickedEventListener;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationViewCreatedEventListener;
import de.luh.kriegel.studip.synchronizer.application.view.EventDashbordView;
import de.luh.kriegel.studip.synchronizer.application.view.MainView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class EventDisplayController
		implements Initializable, NotificationViewCreatedEventListener, NotificationViewClickedEventListener {

	private static final Logger log = LogManager.getLogger(EventDisplayController.class);

	@FXML
	private TabPane root;

	@FXML
	private Tab allEventsTab;

	@FXML
	private Tab downloadEventsTab;

	@FXML
	private Tab courseNewsEventsTab;

	@FXML
	private ListView<EventView> allEventsListView;

	@FXML
	private ListView<EventView> downloadEventsListView;

	@FXML
	private ListView<EventView> courseNewsEventsListView;

	private Callback<ListView<EventView>, ListCell<EventView>> cellFactory;

	private EventManager eventManager;

	private final CourseDownloadFinishedEventListener courseDownloadFinishedEventListener;
	private final CourseNewsReceivedEventListener courseNewsReceivedEventListener;

	private final ObservableList<Event> events = FXCollections.observableArrayList();

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
				events.add(courseDownloadFinishedEvent);
			}
		};

		courseNewsReceivedEventListener = new CourseNewsReceivedEventListener() {
			@Override
			public void onCourseNewsReceived(CourseNewsReceivedEvent courseNewsReceivedEvent) {
				log.info("Received " + courseNewsReceivedEvent.getClass().getSimpleName());
				events.add(courseNewsReceivedEvent);
			}
		};

		cellFactory = new Callback<ListView<EventView>, ListCell<EventView>>() {
			@Override
			public ListCell<EventView> call(ListView<EventView> arg0) {
				return new EventListCell(arg0);
			}
		};

		events.addListener(new ListChangeListener<Event>() {

			EventView eventView;

			@Override
			public void onChanged(Change<? extends Event> arg0) {
				while (arg0.next()) {
					for (Event event : arg0.getAddedSubList()) {

						log.debug("Event received: " + event.getEventType().getSimpleName());

						switch (event.getEventType()) {
						case COURSE_DOWNLOAD_PROGRESS:
							CourseDownloadProgressEvent courseDownloadProgressEvent = (CourseDownloadProgressEvent) event;

							// not of interest for the view
							continue;
						case DOWNLOAD_COMPLETED:
							CourseDownloadFinishedEvent courseDownloadCompletedEvent = (CourseDownloadFinishedEvent) event;
							Platform.runLater(() -> {
								CourseDownloadFinishedEventView courseDownloadCompletedEventView = new CourseDownloadFinishedEventView(
										courseDownloadCompletedEvent, courseDownloadCompletedEvent.getCourse(),
										courseDownloadCompletedEvent.getDownloadedFiles());

								eventView = courseDownloadCompletedEventView;
								downloadEventsListView.getItems().add(eventView);
							});
							break;
						case COURSE_NEWS_RECEIVED:
							CourseNewsReceivedEvent courseNewsReceivedEvent = (CourseNewsReceivedEvent) event;
							Platform.runLater(() -> {
								HtmlEventEntryView courseNewsReceivedEventView = new HtmlEventEntryView(
										courseNewsReceivedEvent.get().getBody_html(), event);

								eventView = courseNewsReceivedEventView;
								courseNewsEventsListView.getItems().add(eventView);
							});
							break;
						case DEFAULT:

							continue;
						}

						Platform.runLater(() -> {
							allEventsListView.getItems().add(eventView);

						});

					}
				}
			}
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		log.debug("Initialize EventDisplayController");

		allEventsListView.setCellFactory(cellFactory);
		downloadEventsListView.setCellFactory(cellFactory);
		courseNewsEventsListView.setCellFactory(cellFactory);

		SynchronizerApp.notificationController.addNotificationViewCreatedEventListener(this);

		SynchronizerApp.studipClient.getCourseService().getDownloadManager()
				.addCourseDownloadFinishedEventListener(courseDownloadFinishedEventListener);
		SynchronizerApp.notificationController.getNotificationManager()
				.addCourseNewsReceivedEventListener(courseNewsReceivedEventListener);

		new Thread(() -> {
			List<Event> allLoggedEvents = eventManager.getAllLoggedEvents();

			Platform.runLater(() -> {
				events.addAll(allLoggedEvents);
			});
		}).start();
	}

	@Override
	public void onNotificationViewClicked(NotificationView notificationView) {
		Event event = notificationView.getEvent();

		SynchronizerApp.simpleWindowStage.show();

		Region region = StageController.getStageById(SynchronizerApp.MAIN_STAGE_ID);

		if (region instanceof MainView) {
			MainView mainView = (MainView) region;

			Node node = mainView.getController().eventRootPane.getChildren().get(0);
			TabPane eventsTabPane = null;
			if (node instanceof EventDashbordView) {
				EventDashbordView eventView = (EventDashbordView) node;

				eventsTabPane = eventView.getTabPane();
			} else {
				return;
			}

			int eventsTabIndex = 0;

			for (int i = 0; i < mainView.getController().tabPane.getTabs().size(); i++) {
				if (mainView.getController().tabPane.getTabs().get(i).getText().toLowerCase().equals("events")) {
					eventsTabIndex = i;
				}
			}
			mainView.getController().tabPane.getSelectionModel().select(eventsTabIndex);

			switch (event.getEventType()) {
			case DOWNLOAD_COMPLETED:
				eventsTabPane.getSelectionModel().select(1);
				break;
			case COURSE_NEWS_RECEIVED:
				eventsTabPane.getSelectionModel().select(2);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onNotificationViewCreated(NotificationView notificationView) {
		notificationView.addNotificationViewClickedEventListener(this);
	}

}
