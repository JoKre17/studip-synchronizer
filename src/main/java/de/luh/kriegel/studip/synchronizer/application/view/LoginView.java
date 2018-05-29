package de.luh.kriegel.studip.synchronizer.application.view;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class LoginView extends StackPane {

	private static final Logger log = LogManager.getLogger(LoginView.class);

	private LoginController controller;

	public LoginView() {

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/LoginPane.fxml"));

		try {
			Region root = loader.load();
			this.getChildren().add(root);

			root.prefWidthProperty().bind(this.widthProperty());
			root.prefHeightProperty().bind(this.heightProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
	}

	public LoginController getController() {
		return controller;
	}
}
