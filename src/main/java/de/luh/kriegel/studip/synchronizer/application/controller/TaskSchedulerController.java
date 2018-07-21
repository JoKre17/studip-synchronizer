package de.luh.kriegel.studip.synchronizer.application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskSchedulerController implements Initializable {

	private final Logger log = LogManager.getLogger(TaskSchedulerController.class);

	private StringProperty passwordProperty = new SimpleStringProperty("");
	private final String TASK_ENABLED_MESSAGE = "Der StudIP Synchronizer wird beim Systemstart gestartet.";
	private final String TASK_DISABLED_MESSAGE = "Kein Task eingerichtet.";
	private final String INSTALL_INFO_MESSAGE = "Um den StudIP Synchronizer als einen Service einzurichten, "
			+ "welcher nach dem Hochfahren automatisch startet, "
			+ "wird das Passwort zum einrichten des dafür notwendigen Tasks benötigt. "
			+ "Solltest du kein Passwort verwenden, kannst du das Feld leerlassen.";

	@FXML
	private Label scheduledTaskStatusLabel;

	@FXML
	private JFXButton taskSchedulerButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(() -> {
			if (ConfigManager.getRunApplicationOnSystemStartProperty().get()) {
				scheduledTaskStatusLabel.setText(TASK_ENABLED_MESSAGE);
				taskSchedulerButton.setText("Uninstall");
			} else {
				scheduledTaskStatusLabel.setText(TASK_DISABLED_MESSAGE);
				taskSchedulerButton.setText("Install");
			}
		});

	}

	@FXML
	private void taskSchedulerButtonClicked(MouseEvent e) throws Exception {

		if (!isAdmin()) {
			new Alert(AlertType.ERROR, "You have to run the StudIP Synchronizer as an Administrator.", ButtonType.CLOSE)
					.show();
			throw new Exception("Not running as Admin");
		}
		
		if(ConfigManager.getRunApplicationOnSystemStartProperty().get()) {
			boolean success = uninstallApplicationAsTask();
			ConfigManager.getRunApplicationOnSystemStartProperty().set(!success);

			Platform.runLater(() -> {
				if (ConfigManager.getRunApplicationOnSystemStartProperty().get()) {
					scheduledTaskStatusLabel.setText(TASK_ENABLED_MESSAGE);
					taskSchedulerButton.setText("Uninstall");
				} else {
					scheduledTaskStatusLabel.setText(TASK_DISABLED_MESSAGE);
					taskSchedulerButton.setText("Install");
				}
			});
			
			return;
		}

		Stage stage = new Stage();
		stage.setMaxWidth(600);
		stage.setWidth(400);
		stage.setHeight(200);

		VBox vbox = new VBox();

		HBox messageHBox = new HBox();
		HBox inputHBox = new HBox();
		HBox optionHBox = new HBox();
		optionHBox.setAlignment(Pos.CENTER);

		Label infoLabel = new Label(INSTALL_INFO_MESSAGE);
		infoLabel.setWrapText(true);

		messageHBox.getChildren().add(infoLabel);

		PasswordField passwordField = new PasswordField();
		inputHBox.getChildren().addAll(new Label("Passwort:"), passwordField);

		Button acceptButton = new Button("OK");
		Button declineButton = new Button("Abbrechen");
		acceptButton.setMaxWidth(200.0);
		declineButton.setMaxWidth(200.0);
		HBox.setHgrow(acceptButton, Priority.ALWAYS);
		HBox.setHgrow(declineButton, Priority.ALWAYS);

		optionHBox.getChildren().addAll(acceptButton, declineButton);

		acceptButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				passwordProperty.set(passwordField.getText());
				stage.hide();
				try {
					boolean success = installApplicationAsTask();
					ConfigManager.getRunApplicationOnSystemStartProperty().set(success);

					Platform.runLater(() -> {
						if (ConfigManager.getRunApplicationOnSystemStartProperty().get()) {
							scheduledTaskStatusLabel.setText(TASK_ENABLED_MESSAGE);
							taskSchedulerButton.setText("Uninstall");
						} else {
							scheduledTaskStatusLabel.setText(TASK_DISABLED_MESSAGE);
							taskSchedulerButton.setText("Install");
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		declineButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				stage.close();
			}
		});

		vbox.getChildren().addAll(messageHBox, inputHBox, optionHBox);

		VBox.setVgrow(messageHBox, Priority.ALWAYS);

		Scene scene = new Scene(vbox);
		stage.setScene(scene);

		stage.show();

	}

	private boolean installApplicationAsTask() throws IOException {

		String username = System.getProperty("user.name");
		String password = passwordProperty.get();

		String executableScript = "\"\\\"" + new File("").getAbsolutePath() + "\\studipSynchronizer.vbs\\\"\" ";

		String schtaskCommand = "cmd /c schtasks /create /tn \"StudIP Synchronizer\" /tr " + executableScript
				+ " /sc onlogon /delay 0001:00 /rl highest /V1 /RU " + username + " /RP " + password;

		Runtime runTime = Runtime.getRuntime();
		Process p = runTime.exec(schtaskCommand);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		boolean errorState = false;

		String out = null;
		String err = null;
		while (((out = stdInput.readLine()) != null) || ((err = stdError.readLine()) != null)) {
			if (out != null) {
				log.info(out);
			}

			if (err != null) {
				errorState = true;
				log.error(err);
			}
		}

		return !errorState;
	}
	
	private boolean uninstallApplicationAsTask() throws IOException {
		
		String username = System.getProperty("user.name");
		String password = passwordProperty.get();

		String executableScript = "\"\\\"" + new File("").getAbsolutePath() + "\\studipSynchronizer.vbs\\\"\" ";

		String schtaskCommand = "cmd /c schtasks /delete /tn \"StudIP Synchronizer\" /f";

		Runtime runTime = Runtime.getRuntime();
		Process p = runTime.exec(schtaskCommand);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		boolean errorState = false;

		String out = null;
		String err = null;
		while (((out = stdInput.readLine()) != null) || ((err = stdError.readLine()) != null)) {
			if (out != null) {
				log.info(out);
			}

			if (err != null) {
				errorState = true;
				log.error(err);
			}
		}

		return !errorState;
	}

	public StringProperty getPasswordProperty() {
		return passwordProperty;
	}

	private static boolean isAdmin() {
		Preferences prefs = Preferences.systemRoot();
		PrintStream systemErr = System.err;
		synchronized (systemErr) { // better synchroize to avoid problems with other threads that access System.err
			System.setErr(null);
			try {
				prefs.put("foo", "bar"); // SecurityException on Windows
				prefs.remove("foo");
				prefs.flush(); // BackingStoreException on Linux
				return true;
			} catch (Exception e) {
				return false;
			} finally {
				System.setErr(systemErr);
			}
		}
	}

}
