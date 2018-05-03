package de.luh.kriegel.studip.synchronizer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.SimpleWindowController;
import de.luh.kriegel.studip.synchronizer.application.view.MainPane;
import javafx.application.Application;
import javafx.stage.Stage;

public class SynchronizerApp extends Application {

	private static final Logger log = LogManager.getLogger(SynchronizerApp.class);

	final private int MIN_WIDTH = 600;
	final private int MIN_HEIGHT = 400;

	public static SimpleWindowStage stage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		stage = new SimpleWindowStage(MIN_WIDTH, MIN_HEIGHT);
		SimpleWindowController controller = stage.getController();

		MainPane mainPane = new MainPane();
		controller.setContent(mainPane);

		stage.show();
	}

}
