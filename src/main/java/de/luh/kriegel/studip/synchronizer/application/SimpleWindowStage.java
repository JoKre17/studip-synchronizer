package de.luh.kriegel.studip.synchronizer.application;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.controller.SimpleWindowController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SimpleWindowStage extends Stage {

	private static final Logger log = LogManager.getLogger(SimpleWindowStage.class);

	private SimpleWindowController controller;

	public SimpleWindowStage(String windowTitle, int MIN_WIDTH, int MIN_HEIGHT) {
		super();

		this.setTitle(windowTitle);
		init(MIN_WIDTH, MIN_HEIGHT);
	}

	public SimpleWindowStage(int MIN_WIDTH, int MIN_HEIGHT) {
		super();

		init(MIN_WIDTH, MIN_HEIGHT);
	}

	public SimpleWindowStage() {
		super();

		// default values
		init(400, 300);
	}

	private void init(double MIN_WIDTH, double MIN_HEIGHT) {
		this.initStyle(StageStyle.TRANSPARENT);
		this.setMinHeight(MIN_HEIGHT);
		this.setMinWidth(MIN_WIDTH);

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/SimpleWindow.fxml"));

		try {
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);

			this.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
		controller.setStage(this);

		this.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					if (getWidth() < getMinWidth()) {
						setWidth(getMinWidth());
					}

					if (getHeight() < getMinHeight()) {
						setHeight(getMinHeight());
					}
				}
			}
		});

		// this.widthProperty().addListener(new ChangeListener<Number>() {
		// @Override
		// public void changed(ObservableValue<? extends Number> observable, Number
		// oldValue, Number newValue) {
		// log.info("Width: " + newValue);
		// }
		// });
		// this.heightProperty().addListener(new ChangeListener<Number>() {
		// @Override
		// public void changed(ObservableValue<? extends Number> observable, Number
		// oldValue, Number newValue) {
		// log.info("Height: " + newValue);
		// }
		// });
	}

	public SimpleWindowController getController() {
		return controller;
	}

}
