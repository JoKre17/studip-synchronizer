package de.luh.kriegel.studip.synchronizer.application.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.auth.Credentials;
import de.luh.kriegel.studip.synchronizer.client.StudIPClient;
import de.luh.kriegel.studip.synchronizer.client.service.AuthService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LoginController implements Initializable {

	private static final Logger log = LogManager.getLogger(LoginController.class);

	@FXML
	private JFXTextField studipUrlField;

	@FXML
	private JFXTextField usernameField;

	@FXML
	private JFXPasswordField passwordField;

	@FXML
	private Label studipUrlInfoLabel;

	@FXML
	private Label usernameInfoLabel;

	@FXML
	private Label passwordInfoLabel;

	private final StringProperty passwordProperty = new SimpleStringProperty("");
	
	private boolean testRun = true;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		passwordProperty.bind(passwordField.textProperty());

		studipUrlField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue != null && newValue.length() > 0) {

					try {
						URL baseUri = new URL(studipUrlField.getText());
						String host = baseUri.getHost();
						String protocol = baseUri.getProtocol();
						URL baseUrl = new URL(protocol + "://" + host);
						log.debug(baseUrl);

						Platform.runLater(() -> {
							studipUrlInfoLabel.setText("");
						});
					} catch (MalformedURLException e) {
						Platform.runLater(() -> {
							studipUrlInfoLabel.setText("The URL is not valid!");
						});
					}

				}
			}
		});

		usernameField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					if (newValue.length() == 0) {
						Platform.runLater(() -> {
							usernameInfoLabel.setText("The length must be greater than 0!");
						});
					} else {
						Platform.runLater(() -> {
							usernameInfoLabel.setText("");
						});
					}
				}
			}
		});

		passwordProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					if (newValue.length() == 0) {
						Platform.runLater(() -> {
							passwordInfoLabel.setText("The length must be greater than 0!");
						});
					} else {
						Platform.runLater(() -> {
							passwordInfoLabel.setText("");
						});
					}
				}
			}
		});

		if(testRun) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Platform.runLater(() -> {
						studipUrlField.setText("https://studip.uni-hannover.de");
						usernameField.setText("JK_14");
						passwordField.setText("Aiedail95");
						
						try {
							login();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					});
				}
				
			}).start();
		}

	}

	@FXML
	private void keyPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			try {
				login();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}

	@FXML
	private void loginButtonClicked(MouseEvent e) {
		try {
			login();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	private void login() throws URISyntaxException {
		log.info("Performing login!");
		SynchronizerApp.simpleWindowStage.getController().setStatus("Performing login!");

		boolean urlIsMissing = studipUrlField.getText().isEmpty();
		boolean usernameIsMissing = usernameField.getText().isEmpty();
		boolean passwordIsMissing = passwordField.getText().isEmpty();

		if (urlIsMissing || usernameIsMissing || passwordIsMissing) {
			SynchronizerApp.simpleWindowStage.getController().setStatus("Missing data");

			if (urlIsMissing) {
				studipUrlInfoLabel.setText("Value missing!");
			}

			if (usernameIsMissing) {
				usernameInfoLabel.setText("Value missing!");
			}

			if (passwordIsMissing) {
				passwordInfoLabel.setText("Value missing!");
			}
			return;
		}

		URI baseUri = null;
		try {
			URL baseUrl = new URL(studipUrlField.getText());
			String host = baseUrl.getHost();
			String protocol = baseUrl.getProtocol();
			baseUrl = new URL(protocol + "://" + host);
			baseUri = baseUrl.toURI();
			log.info(baseUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			studipUrlInfoLabel.setText("Malformed URL");
		}

		studipUrlField.setText(baseUri.toString());

		String username = usernameField.getText();
		String password = passwordProperty.getValue();

		Credentials credentials = new Credentials(username, password);
		log.info("Base URL: " + studipUrlField.getText());
		log.info(credentials);

		SynchronizerApp.studipClient = new StudIPClient(baseUri, credentials);
		AuthService authService = SynchronizerApp.studipClient.getAuthService();

		boolean isSuccessfullyAuthenticated = authService.authenticate();
		if (!isSuccessfullyAuthenticated) {
			log.info(authService.getAuthErrorResponse());
		}

		if (isSuccessfullyAuthenticated) {
			SynchronizerApp.simpleWindowStage.getController().setStatus("Logged in");
			
			StageController.setStage(SynchronizerApp.MAIN_STAGE_ID);
//			StageController.setStage("SETTINGS_STAGE");
		} else {
			SynchronizerApp.simpleWindowStage.getController().setStatus(authService.getAuthErrorResponse());
		}

		SynchronizerApp.simpleWindowStage.getController().clearStatus();
	}

}
