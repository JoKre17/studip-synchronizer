package de.luh.kriegel.studip.synchronizer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import de.luh.kriegel.studip.synchronizer.application.controller.StageController;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationController;
import de.luh.kriegel.studip.synchronizer.application.view.LoginView;
import de.luh.kriegel.studip.synchronizer.client.StudIPClient;
import de.luh.kriegel.studip.synchronizer.download.SynchronizeTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SynchronizerApp extends Application {

	private static final Logger log = LogManager.getLogger(SynchronizerApp.class);

	final private int MIN_WIDTH = 800;
	final private int MIN_HEIGHT = 600;

	public static final Image ICON = new Image(
			SynchronizerApp.class.getClassLoader().getResourceAsStream("images/studip-synchronizer.jpg"));
	public static final Image ICON_TRANSPARENT = new Image(
			SynchronizerApp.class.getClassLoader().getResourceAsStream("images/studip-synchronizer.png"));

	public static final String LOGIN_STAGE_ID = "LOGIN_STAGE";
	public static final String MAIN_STAGE_ID = "MAIN_STAGE";

	public static SimpleWindowStage simpleWindowStage;
	public static StudIPClient studipClient;
	public static ConfigManager configManager = new ConfigManager();
	public static NotificationController notificationController;

	public static SynchronizeTimer synchronizerTimer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		Platform.setImplicitExit(false);

		simpleWindowStage = new SimpleWindowStage("StudIP Synchronizer", MIN_WIDTH, MIN_HEIGHT);
		simpleWindowStage.getIcons().add(ICON);

		StageController.setSimpleWindowStage(simpleWindowStage);

		LoginView loginView = new LoginView();
		StageController.addRegionToStagingMap("LOGIN_STAGE", loginView);

		StageController.setStage("LOGIN_STAGE");

		simpleWindowStage.show();
	}

}
