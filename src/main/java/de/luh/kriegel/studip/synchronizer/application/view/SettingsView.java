package de.luh.kriegel.studip.synchronizer.application.view;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;

public class SettingsView extends ScrollPane {

	private static final Logger log = LogManager.getLogger(SettingsView.class);

	private SettingsController controller;

	public SettingsView() {
		this.hbarPolicyProperty().setValue(ScrollBarPolicy.NEVER);
		this.vbarPolicyProperty().setValue(ScrollBarPolicy.AS_NEEDED);
		this.setFitToWidth(true);
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/SettingsPane.fxml"));
		
		try {
			Region root = loader.load();
			this.setContent(root);

			root.prefWidthProperty().bind(this.widthProperty());
//			root.prefHeightProperty().bind(this.heightProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
	}
	
	public SettingsController getController() {
		return controller;
	}
	
}
