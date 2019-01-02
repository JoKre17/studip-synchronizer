package de.kriegel.studip.synchronizer.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

public class MainController {

	private final static Logger log = LoggerFactory.getLogger(MainController.class);

	@FXML
	TabPane tabPane;
	
	@FXML
	AnchorPane settingsRootPane;

	@FXML
	AnchorPane taskSchedulerRootPane;

	@FXML
	AnchorPane eventRootPane;

	@FXML
	AnchorPane aboutRootPane;

	public void setSettingsPane(Region settingsPane) {
		settingsRootPane.getChildren().clear();
		settingsRootPane.getChildren().add(settingsPane);

		setAllAnchors(settingsPane, 0.0);
	}

	public void setTaskSchedulerPane(Region taskSchedulerPane) {
		taskSchedulerRootPane.getChildren().clear();
		taskSchedulerRootPane.getChildren().add(taskSchedulerPane);

		setAllAnchors(taskSchedulerPane, 0.0);
	}

	public void setEventPane(Region eventPane) {
		eventRootPane.getChildren().clear();
		eventRootPane.getChildren().add(eventPane);

		setAllAnchors(eventPane, 0.0);
	}

	public void setAboutPane(Region aboutPane) {
		aboutRootPane.getChildren().clear();
		aboutRootPane.getChildren().add(aboutPane);

		setAllAnchors(aboutPane, 0.0);
	}

	private void setAllAnchors(Region region, double anchorValue) {
		AnchorPane.setTopAnchor(region, anchorValue);
		AnchorPane.setRightAnchor(region, anchorValue);
		AnchorPane.setBottomAnchor(region, anchorValue);
		AnchorPane.setLeftAnchor(region, anchorValue);
	}

}
