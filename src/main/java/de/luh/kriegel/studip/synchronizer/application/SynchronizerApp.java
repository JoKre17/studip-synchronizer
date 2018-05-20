package de.luh.kriegel.studip.synchronizer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.SimpleWindowController;
import de.luh.kriegel.studip.synchronizer.application.view.LoginView;
import de.luh.kriegel.studip.synchronizer.application.view.SettingsView;
import de.luh.kriegel.studip.synchronizer.client.StudIPClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SynchronizerApp extends Application {

	private static final Logger log = LogManager.getLogger(SynchronizerApp.class);

	final private int MIN_WIDTH = 600;
	final private int MIN_HEIGHT = 400;

	public static SimpleWindowStage stage;
	public static StudIPClient studipClient;
	public static SettingsView settingsView;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		stage = new SimpleWindowStage("StudIP Synchronizer", MIN_WIDTH, MIN_HEIGHT);
		stage.getIcons()
				.add(new Image(getClass().getClassLoader().getResourceAsStream("images/studip-synchronizer.jpg")));
		SimpleWindowController controller = stage.getController();

		LoginView loginView = new LoginView();
		controller.setContent(loginView);

		settingsView = new SettingsView();

		stage.show();

		Platform.runLater(() -> {
			log.info("Stage: " + stage.getWidth());
			log.info("Scene: " + stage.getScene().getWidth());
			log.info("Root: " + stage.getScene().getRoot().prefWidth(800));
			log.info("Child: " + stage.getScene().getRoot().getChildrenUnmodifiable().get(0).getId() + " "
					+ stage.getScene().getRoot().getChildrenUnmodifiable().get(0).prefWidth(0));
		});
	}

}
