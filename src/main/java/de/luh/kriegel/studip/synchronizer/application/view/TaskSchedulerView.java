package de.luh.kriegel.studip.synchronizer.application.view;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.TaskSchedulerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;

public class TaskSchedulerView extends ScrollPane {

	private static final Logger log = LogManager.getLogger(SettingsView.class);

	private TaskSchedulerController controller;

	public TaskSchedulerView() {
		this.hbarPolicyProperty().setValue(ScrollBarPolicy.NEVER);
		this.vbarPolicyProperty().setValue(ScrollBarPolicy.AS_NEEDED);
		this.setFitToWidth(true);
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/TaskSchedulerPane.fxml"));
		
		try {
			Region root = loader.load();
			this.setContent(root);

			root.prefWidthProperty().bind(this.widthProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
	}
	
	public TaskSchedulerController getController() {
		return controller;
	}
	
	
}

