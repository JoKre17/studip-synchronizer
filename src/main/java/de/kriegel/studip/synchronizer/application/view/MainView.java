package de.kriegel.studip.synchronizer.application.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kriegel.studip.synchronizer.application.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainView extends VBox implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(MainView.class);

	private MainController mainController;
	private SettingsView settingsView;
	private TaskSchedulerView taskSchedulerView;
	private EventDashbordView eventView;

	public MainView() {

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MainPane.fxml"));

		try {
			Region root = loader.load();
			this.getChildren().add(root);

			root.prefWidthProperty().bind(this.widthProperty());
			root.prefHeightProperty().bind(this.heightProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}

		mainController = loader.getController();

		// this.setStyle("-fx-background-color: transparent;");

		initialize();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialize();
	}

	private void initialize() {
		settingsView = new SettingsView();
		taskSchedulerView = new TaskSchedulerView();
		eventView = new EventDashbordView();

		mainController.setSettingsPane(settingsView);
		mainController.setTaskSchedulerPane(taskSchedulerView);
		mainController.setEventPane(eventView);
	}

	public MainController getController() {
		return mainController;
	}

	public SettingsView getSettingsView() {
		return settingsView;
	}

	public TaskSchedulerView getTaskSchedulerView() {
		return taskSchedulerView;
	}

	public EventDashbordView getEventView() {
		return eventView;
	}

}
