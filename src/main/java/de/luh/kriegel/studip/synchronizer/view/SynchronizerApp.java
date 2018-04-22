package de.luh.kriegel.studip.synchronizer.view;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class SynchronizerApp extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Circle circ = new Circle(40, 40, 30);
		Group root = new Group(circ);
		
		
		Scene scene = new Scene(root, 400, 300);

		stage.setTitle("StudIP Synchronizer");
		stage.setScene(scene);
		stage.show();
	}

}
