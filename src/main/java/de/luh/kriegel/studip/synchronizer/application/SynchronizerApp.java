package de.luh.kriegel.studip.synchronizer.application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SynchronizerApp extends Application {

	private static final String STUDIP_COLOR_BLUE_RGB = "rgb(40,73,124)";

	final private int MINWIDTH = 650, MINHEGIHT = 500;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		// FXMLLoader loader = new
		// FXMLLoader(getClass().getClassLoader().getResource("fxml/MainWindow.fxml"));
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/synchronizer.fxml"));

		AnchorPane root = loader.load();
		root.setBackground(Background.EMPTY);

		// BorderPane b = new BorderPane();
		Scene scene = new Scene(root);

		// MainPane mainPane = new MainPane();

		// Setting the model for the controller
		// ((MainWindowCtrl) loader.getController()).setModel(m);

		// Creating the style for the custom window
		// MinimalWindowCtrl minimalWindowCtrl = new MinimalWindowCtrl(primaryStage,
		// MINWIDTH, MINHEGIHT);
		// minimalWindowCtrl.setContent(mainPane);
		// minimalWindowCtrl.setTitle("Smart Station");

		// Making new scene
		// Scene scene = new Scene(minimalWindowCtrl, MINWIDTH, MINHEGIHT);
		// scene.setFill(null);

		// Setting the style to the window (undecorating it)
		primaryStage.initStyle(StageStyle.UNDECORATED);

		// Setting the scene on the window
		primaryStage.setScene(scene);

		// Showing the window
		primaryStage.show();

		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
