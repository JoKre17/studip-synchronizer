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

import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import javafx.beans.property.BooleanProperty;
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
							ConfigManager.getSynchronizationIntervalProperty().set((Integer) userData);
						}
					}
				}
			});

			// set initial value
			Object userData = radioButton.getUserData();
			if(userData instanceof Integer) {
				int synchInterval = (int) userData;
				if(synchInterval == ConfigManager.getSynchronizationIntervalProperty().get()) {
					radioButton.selectedProperty().set(true);
				} else {
					radioButton.selectedProperty().set(false);
				}
			}
		}

		downloadDirectoryBrowseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				File dir = new DirectoryChooser().showDialog(null);
				if (dir != null) {
					downloadDirectoryLabel.setText(dir.getAbsolutePath());
				}
			}
		});

		downloadEnabledToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			}
		});

		notificationEnabledToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			}
		});
		
		downloadEnabledToggleButton.selectedProperty().set(ConfigManager.getDownloadEnabledProperty().get());
		downloadDirectoryLabel.setText(ConfigManager.getDownloadDirectoryPathProperty().get());
		
		notificationEnabledToggleButton.selectedProperty().set(ConfigManager.getNotificationsEnabledProperty().get());
		
		ConfigManager.getDownloadEnabledProperty().bind(downloadEnabledToggleButton.selectedProperty());
		ConfigManager.getDownloadDirectoryPathProperty().bind(downloadDirectoryLabel.textProperty());

		ConfigManager.getNotificationsEnabledProperty().bind(notificationEnabledToggleButton.selectedProperty());
	}

	public BooleanProperty getDownloadEnabledProperty() {
		return downloadEnabledToggleButton.selectedProperty();
	}

	public boolean isDownloadEnabled() {
		return downloadEnabledToggleButton.isArmed();
	}

}
