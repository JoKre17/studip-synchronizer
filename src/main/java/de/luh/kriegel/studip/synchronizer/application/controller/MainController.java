package de.luh.kriegel.studip.synchronizer.application.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

public class MainController {

	private final static Logger log = LogManager.getLogger(MainController.class);
	
	@FXML
	AnchorPane settingsRootPane;

	@FXML
	AnchorPane taskSchedulerRootPane;

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
