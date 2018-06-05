package de.luh.kriegel.studip.synchronizer.application.controller;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

public class SettingsController implements Initializable {

	private final static Logger log = LogManager.getLogger(SettingsController.class);

	@FXML
	private TabPane tabPane;

	// UI Elements //

	// Download
	@FXML
	private JFXToggleButton downloadEnabledToggleButton;

	@FXML
	private JFXRadioButton oncePerStartRadioButton;

	@FXML
	private JFXRadioButton fiveMinRadioButton;

	@FXML
	private JFXRadioButton fifteenMinRadioButton;

	@FXML
	private JFXRadioButton thirtyMinRadioButton;

	@FXML
	private Label downloadDirectoryLabel;

	@FXML
	private JFXButton downloadDirectoryBrowseButton;

	// Notification

	@FXML
	private JFXToggleButton notificationEnabledToggleButton;

	private List<JFXRadioButton> synchronizeIntervalRadioButtons;

	private final ReadOnlyIntegerWrapper synchronizeIntervalWrapper = new ReadOnlyIntegerWrapper();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Download

		// Set UserData for Radio Buttons
		oncePerStartRadioButton.setUserData(new Integer(0));
		fiveMinRadioButton.setUserData(new Integer(5));
		fifteenMinRadioButton.setUserData(new Integer(15));
		thirtyMinRadioButton.setUserData(new Integer(30));

		synchronizeIntervalRadioButtons = Arrays.asList(oncePerStartRadioButton, fiveMinRadioButton,
				fifteenMinRadioButton, thirtyMinRadioButton);

		// Set Listener for Radio Buttons
		for (JFXRadioButton radioButton : synchronizeIntervalRadioButtons) {
			radioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue != null && newValue.booleanValue()) {
						Object userData = radioButton.getUserData();
						if (userData instanceof Integer) {
							synchronizeIntervalWrapper.set((Integer) userData);
						}
					}
				}
			});

			// set initial value
			if (radioButton.isSelected()) {
				Object userData = radioButton.getUserData();
				if (userData instanceof Integer) {
					synchronizeIntervalWrapper.set((Integer) userData);
				}
			}
		}

		downloadDirectoryBrowseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				File dir = new DirectoryChooser().showDialog(null);
				if (dir != null) {
					log.info("Download Dir: " + dir.getAbsolutePath());
				}
			}
		});

		downloadEnabledToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				log.info("Download Enabled: " + newValue);
			}
		});

		synchronizeIntervalWrapper.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				log.info("Synch Interval: " + newValue);
			}
		});

		notificationEnabledToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				log.info("Notification Enabled: " + newValue);
			}
		});
	}

	public BooleanProperty getDownloadEnabledProperty() {
		return downloadEnabledToggleButton.selectedProperty();
	}

	public boolean isDownloadEnabled() {
		return downloadEnabledToggleButton.isArmed();
	}

	public int getSynchronizeInterval() {
		return synchronizeIntervalWrapper.get();
	}

	public ReadOnlyIntegerProperty getSynchronizeIntervalProperty() {
		return synchronizeIntervalWrapper.getReadOnlyProperty();
	}

}
