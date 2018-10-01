package de.luh.kriegel.studip.synchronizer.application.controller;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import de.luh.kriegel.studip.synchronizer.application.notification.NotificationController;
import de.luh.kriegel.studip.synchronizer.download.SynchronizeTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
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

	private NotificationController notificationController;

	// UI Elements //

	@FXML
	private TabPane tabPane;

	// Download
	@FXML
	private JFXToggleButton downloadEnabledToggleButton;

	@FXML
	private Label nextSynchAtLabel;

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

	// Application

	@FXML
	private JFXToggleButton minimizedStartEnabledToggleButton;

	@FXML
	private JFXToggleButton rememberMeEnabledToggleButton;

	private List<JFXRadioButton> synchronizeIntervalRadioButtons;

	private void downloadEnabledUpdated(boolean downloadEnabled) {

		Platform.runLater(() -> {
			StringProperty downloadDirectoryPathProperty = ConfigManager.getDownloadDirectoryPathProperty();
			if (downloadDirectoryPathProperty.get().equals("")) {
				downloadDirectoryLabel.setText(SynchronizerApp.studipClient.getCourseService().getDownloadManager()
						.getDownloadDirectory().getAbsolutePath());
			} else {
				downloadDirectoryLabel.setText(ConfigManager.getDownloadDirectoryPathProperty().get());
			}
		});

		if (downloadEnabled) {
			int timeUntilNextSynchronizationInMilliseconds = (int) SynchronizeTimer.SLEEP_TIME_BEFORE_FIRST_SYNCH_IN_MILLIS;
			int synchIntervallInMinutes = ConfigManager.getSynchronizationIntervalProperty().get();

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MILLISECOND, timeUntilNextSynchronizationInMilliseconds);
			// c.add(Calendar.MINUTE, synchIntervallInMinutes);

			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			String content = format.format(c.getTime());

			Platform.runLater(() -> {
				nextSynchAtLabel.setText("Next run at " + content + " (local time)");
			});

			if (SynchronizerApp.synchronizerTimer != null) {
				SynchronizerApp.synchronizerTimer.interrupt();
			}

			File downloadDirectory = new File(ConfigManager.getDownloadDirectoryPathProperty().get());

			if (downloadDirectory.exists() && downloadDirectory.isDirectory()) {
				SynchronizerApp.studipClient.getCourseService().getDownloadManager()
						.setDownloadDirectory(downloadDirectory);
				SynchronizerApp.synchronizerTimer = new SynchronizeTimer(SynchronizerApp.studipClient,
						ConfigManager.getSynchronizationIntervalProperty().get() * 60000);
				SynchronizerApp.synchronizerTimer.start();
			}
		} else {
			Platform.runLater(() -> {
				nextSynchAtLabel.setText("No run scheduled");
			});

			if (SynchronizerApp.synchronizerTimer != null) {
				SynchronizerApp.synchronizerTimer.interrupt();
				SynchronizerApp.synchronizerTimer = null;
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Download

		// Set UserData for Radio Buttons
		oncePerStartRadioButton.setUserData(0);
		fiveMinRadioButton.setUserData(5);
		fifteenMinRadioButton.setUserData(15);
		thirtyMinRadioButton.setUserData(30);

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
			if (userData instanceof Integer) {
				int synchInterval = (int) userData;
				if (synchInterval == ConfigManager.getSynchronizationIntervalProperty().get()) {
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
				downloadEnabledUpdated(newValue);
			}
		});

		notificationEnabledToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			}
		});

		try {
			notificationController = new NotificationController();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called by LoginController after login has been performed
	 */
	public void loginPerformed() {
		downloadEnabledToggleButton.selectedProperty().set(ConfigManager.getDownloadEnabledProperty().get());
		downloadDirectoryLabel.setText(ConfigManager.getDownloadDirectoryPathProperty().get());

		notificationEnabledToggleButton.selectedProperty().set(ConfigManager.getNotificationsEnabledProperty().get());

		minimizedStartEnabledToggleButton.selectedProperty()
				.set(ConfigManager.getMinimizedStartOfApplicationProperty().get());
		rememberMeEnabledToggleButton.selectedProperty().set(ConfigManager.getRememberMeEnabledProperty().get());

		ConfigManager.getDownloadEnabledProperty().bind(downloadEnabledToggleButton.selectedProperty());
		ConfigManager.getDownloadDirectoryPathProperty().bind(downloadDirectoryLabel.textProperty());

		ConfigManager.getNotificationsEnabledProperty().bind(notificationEnabledToggleButton.selectedProperty());

		ConfigManager.getMinimizedStartOfApplicationProperty()
				.bind(minimizedStartEnabledToggleButton.selectedProperty());
		ConfigManager.getRememberMeEnabledProperty().bind(rememberMeEnabledToggleButton.selectedProperty());

		notificationController.loginPerformed();
	}

	public BooleanProperty getDownloadEnabledProperty() {
		return downloadEnabledToggleButton.selectedProperty();
	}

	public boolean isDownloadEnabled() {
		return downloadEnabledToggleButton.isArmed();
	}

}
