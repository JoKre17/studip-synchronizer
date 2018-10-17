package de.luh.kriegel.studip.synchronizer.application.view;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXTabPane;

import de.luh.kriegel.studip.synchronizer.application.controller.EventDisplayController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class EventView extends StackPane {

	private static final Logger log = LogManager.getLogger(SettingsView.class);

	private EventDisplayController controller;

	public EventView() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/EventDisplayPane.fxml"));

		try {
			JFXTabPane root = loader.load();
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

}
