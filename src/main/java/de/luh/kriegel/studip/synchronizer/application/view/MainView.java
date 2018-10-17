package de.luh.kriegel.studip.synchronizer.application.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainView extends VBox implements Initializable {

	private static final Logger log = LogManager.getLogger(MainView.class);

	private MainController mainController;
	private SettingsView settingsView;
	private TaskSchedulerView taskSchedulerView;
	private EventView eventView;

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
		eventView = new EventView();

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

	public EventView getEventView() {
		return eventView;
	}

}
