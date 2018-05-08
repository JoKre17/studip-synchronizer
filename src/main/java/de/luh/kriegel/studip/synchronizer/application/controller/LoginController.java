package de.luh.kriegel.studip.synchronizer.application.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class LoginController implements Initializable {

	private static final Logger log = LogManager.getLogger(LoginController.class);

	@FXML
	private TextField studipUrlField;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField passwordField;

	@FXML
	private Label studipUrlInfoLabel;

	@FXML
	private Label usernameInfoLabel;

	@FXML
	private Label passwordInfoLabel;

	private final StringProperty passwordProperty = new SimpleStringProperty("");

	private StudIPClient studipClient;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		passwordField.textProperty().bind(passwordProperty);

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

		passwordField.setTextFormatter(new TextFormatter<StringProperty>(new StringConverter<StringProperty>() {

			@Override
			public String toString(StringProperty stringProperty) {
				if (stringProperty == null) {
					return "";
				}

				return stringProperty.getValue().replaceAll(".", "*");
			}

			@Override
			public StringProperty fromString(String string) {
				StringProperty sp = new SimpleStringProperty(string);
				sp.setValue(string);
				return sp;
			}

		}));

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

	private KeyEvent lastKeyPressedEvent = null;

	@FXML
	private void passwordFieldKeyPressed(KeyEvent e) {
		lastKeyPressedEvent = e;
	}

	@FXML
	private void passwordFieldKeyTyped(KeyEvent e) {
		KeyCode c;
		if (lastKeyPressedEvent != null) {
			c = lastKeyPressedEvent.getCode();
		} else {
			c = KeyCode.UNDEFINED;
		}

		// log.debug(c.isDigitKey() + " " + c.isLetterKey());
		if (c.isDigitKey() || c.isLetterKey()) {
			// log.debug(!c.isFunctionKey() + " " + !c.isArrowKey() + " " + !c.isMediaKey()
			// + " " + !c.isNavigationKey()
			// + " " + !c.isWhitespaceKey());
			if (!c.isFunctionKey() && !c.isArrowKey() && !c.isMediaKey() && !c.isNavigationKey()
					&& !c.isWhitespaceKey()) {
				// log.debug(!e.isControlDown() + " " + !e.isAltDown());
				if (!e.isControlDown() && !e.isAltDown()) {

					passwordProperty.setValue(passwordProperty.getValue() + e.getCharacter());
				}
			}
		}

		if (c.equals(KeyCode.DELETE) || c.equals(KeyCode.BACK_SPACE)) {
			IndexRange selection = passwordField.getSelection();
			String passwordString = passwordProperty.getValue();

			if ((selection.getEnd() - selection.getStart()) == 0) {
				passwordProperty.setValue(passwordString.substring(0, Math.max(passwordString.length() - 1, 0)));
			} else {
				passwordString = passwordString.substring(0, selection.getStart())
						+ passwordString.substring(selection.getEnd(), passwordString.length());
				passwordProperty.setValue(passwordString);
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
		SynchronizerApp.stage.getController().setStatus("Performing login!");

		boolean urlIsMissing = studipUrlField.getText().isEmpty();
		boolean usernameIsMissing = usernameField.getText().isEmpty();
		boolean passwordIsMissing = passwordField.getText().isEmpty();

		if (urlIsMissing || usernameIsMissing || passwordIsMissing) {
			SynchronizerApp.stage.getController().setStatus("Missing data");

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

		studipClient = new StudIPClient(baseUri, credentials);
		AuthService authService = studipClient.getAuthService();

		boolean isSuccessfullyAuthenticated = authService.authenticate();
		if (!isSuccessfullyAuthenticated) {
			log.info(authService.getAuthErrorResponse());
		}

		if (isSuccessfullyAuthenticated) {
			SynchronizerApp.stage.getController().setStatus("Logged in");

			// TODO
			// Switch pane
		} else {
			SynchronizerApp.stage.getController().setStatus(authService.getAuthErrorResponse());
		}

		SynchronizerApp.stage.getController().clearStatus();
	}

}
