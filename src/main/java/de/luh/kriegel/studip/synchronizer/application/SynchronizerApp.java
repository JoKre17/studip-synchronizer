package de.luh.kriegel.studip.synchronizer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.StageController;
import de.luh.kriegel.studip.synchronizer.application.view.LoginView;
import de.luh.kriegel.studip.synchronizer.application.view.MainView;
import de.luh.kriegel.studip.synchronizer.client.StudIPClient;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SynchronizerApp extends Application {

	private static final Logger log = LogManager.getLogger(SynchronizerApp.class);

	final private int MIN_WIDTH = 600;
	final private int MIN_HEIGHT = 400;

	public static final String LOGIN_STAGE_ID = "LOGIN_STAGE";
	public static final String MAIN_STAGE_ID = "MAIN_STAGE";
	
	public static SimpleWindowStage simpleWindowStage;
	public static StudIPClient studipClient;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		simpleWindowStage = new SimpleWindowStage("StudIP Synchronizer", MIN_WIDTH, MIN_HEIGHT);
		simpleWindowStage.getIcons()
				.add(new Image(getClass().getClassLoader().getResourceAsStream("images/studip-synchronizer.jpg")));
		
		StageController.setSimpleWindowStage(simpleWindowStage);

		LoginView loginView = new LoginView();
		StageController.addRegionToStagingMap("LOGIN_STAGE", loginView);
		
		MainView mainView = new MainView();
		StageController.addRegionToStagingMap("MAIN_STAGE", mainView);
		
		StageController.setStage("LOGIN_STAGE");
	}
	
}
