package de.kriegel.studip.synchronizer.application.view;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kriegel.studip.synchronizer.application.controller.EventDisplayController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class EventDashbordView extends StackPane {

	private static final Logger log = LogManager.getLogger(SettingsView.class);

	private EventDisplayController controller;

	private TabPane root;
	
	public EventDashbordView() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/EventDisplayPane.fxml"));

		try {
			root = loader.load();
			this.getChildren().add(root);

			root.prefWidthProperty().bind(this.widthProperty());
			root.prefHeightProperty().bind(this.heightProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
	}

	public EventDisplayController getController() {
		return controller;
	}
	
	public TabPane getTabPane() {
		return root;
	}

}
