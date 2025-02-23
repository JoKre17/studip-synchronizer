package de.kriegel.studip.synchronizer.application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.kriegel.studip.client.auth.Credentials;
import de.kriegel.studip.client.service.AuthService;
import de.kriegel.studip.client.service.StudIPClient;
import de.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.kriegel.studip.synchronizer.application.config.ConfigManager;
import de.kriegel.studip.synchronizer.application.model.CustomServerPair;
import de.kriegel.studip.synchronizer.application.view.MainView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class LoginController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@FXML
	private JFXTextField studipUrlField;

	@FXML
	private JFXTextField usernameField;

	@FXML
	private JFXPasswordField passwordField;

	@FXML
	private JFXComboBox<CustomServerPair> studipUrlComboBox;

	private ChangeListener<CustomServerPair> studipURLComboBoxListner = new ChangeListener<CustomServerPair>() {
		@Override
		public void changed(ObservableValue<? extends CustomServerPair> observable, CustomServerPair oldValue,
				CustomServerPair newValue) {
			if (newValue != null && newValue.getValue() != null) {
				studipUrlField.setText(newValue.getValue().toString());

				ConfigManager.getStudipServerNameProperty().set(newValue.getKey());
				ConfigManager.getStudipUrlProperty().set(studipUrlField.getText());
			}
		}
	};

	@FXML
	private JFXCheckBox studipUrlOtherCheckBox;

	@FXML
	private Label studipUrlInfoLabel;

	@FXML
	private Label usernameInfoLabel;

	@FXML
	private Label passwordInfoLabel;

	@FXML
	private JFXCheckBox rememberMeCheckBox;

	private boolean testRun = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			InputStream studipServerAddressesInputStream = getClass().getClassLoader()
					.getResourceAsStream("config/studipServerAddresses.conf");
			InputStreamReader isr = new InputStreamReader(studipServerAddressesInputStream, "UTF-8");

			List<String> lines = new BufferedReader(isr).lines().collect(Collectors.toList());

			ObservableList<CustomServerPair> items = FXCollections.observableArrayList();
			for (String line : lines) {
				if (line.indexOf("=") < 0) {
					continue;
				}

				String[] split = line.split("=");
				String serverName = split[0];
				URL url;
				try {
					url = new URL(split[1]);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					log.error("Could not add " + serverName + " => " + split[1] + " to the list.");
					continue;
				}
				items.add(new CustomServerPair(serverName, url));
			}
			studipUrlComboBox.setItems(items);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		studipUrlComboBox.setCellFactory(new Callback<ListView<CustomServerPair>, ListCell<CustomServerPair>>() {
			@Override
			public ListCell<CustomServerPair> call(ListView<CustomServerPair> p) {
				return new ListCell<CustomServerPair>() {
					VBox vbox = new VBox();
					Label serverName = new Label("(Unknown)");
					Label url = new Label("(Unknown)");

					{
						setContentDisplay(ContentDisplay.CENTER);
						vbox.getChildren().addAll(serverName, url);
					}

					@Override
					protected void updateItem(CustomServerPair item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null || empty) {
							setGraphic(null);
						}

						if (item != null) {
							serverName.setText(item.getKey());
							url.setText(item.getValue().toString());
							setGraphic(vbox);

						}
					}

				};
			}
		});

		studipUrlField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue != null && newValue.length() > 0) {

					ConfigManager.getStudipUrlProperty().set(newValue);

					try {
						URL baseUri = new URL(studipUrlField.getText());
						String host = baseUri.getHost();
						String protocol = baseUri.getProtocol();
						URL baseUrl = new URL(protocol + "://" + host);

						ConfigManager.getStudipServerNameProperty().set("");
						ConfigManager.getStudipUrlProperty().set(studipUrlField.getText());

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

		ConfigManager.getPasswordProperty().addListener(new ChangeListener<String>() {
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

		/*
		 * if (ConfigManager.getStudipServerNameProperty().get().equals("") &&
		 * studipUrlComboBox.getItems().size() > 0) {
		 * studipUrlComboBox.getSelectionModel().select(0); log.debug("if catch UI: " +
		 * studipUrlField.getText() + " Property: " +
		 * ConfigManager.getStudipUrlProperty().get());
		 * studipUrlField.setText(studipUrlComboBox.getItems().get(0).getValue().
		 * toString()); }
		 */

		// if url matches with one of the predefined servers, then select it
		for (int i = 0; i < studipUrlComboBox.getItems().size(); i++) {
			if (ConfigManager.getStudipUrlProperty().get()
					.equals(studipUrlComboBox.getItems().get(i).getValue().toString())) {
				studipUrlComboBox.getSelectionModel().select(i);
				log.debug("for UI: " + studipUrlField.getText() + " Property: "
						+ ConfigManager.getStudipUrlProperty().get());
				studipUrlField.setText(studipUrlComboBox.getItems().get(i).getValue().toString());
				break;
			}
		}

		studipUrlOtherCheckBox.selectedProperty().set(ConfigManager.getStudipOtherUrlEnabledProperty().get());
		log.debug("last UI: " + studipUrlField.getText() + " Property: " + ConfigManager.getStudipUrlProperty().get());
		studipUrlField.setText(ConfigManager.getStudipUrlProperty().get());
		usernameField.setText(ConfigManager.getUsernameProperty().get());
		passwordField.setText(ConfigManager.getPasswordProperty().get());
		rememberMeCheckBox.selectedProperty().set(ConfigManager.getRememberMeEnabledProperty().get());

		studipUrlOtherCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				studipUrlComboBox.setDisable(newValue);
				studipUrlField.setDisable(!newValue);

				if (newValue) {
					studipUrlComboBox.getSelectionModel().selectedItemProperty()
							.removeListener(studipURLComboBoxListner);
				} else {
					studipUrlComboBox.getSelectionModel().selectedItemProperty().addListener(studipURLComboBoxListner);
				}

			}
		});

		rememberMeCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					usernameField.setText(usernameField.getText());
				}
			}
		});

		studipUrlComboBox.setDisable(studipUrlOtherCheckBox.selectedProperty().get());
		studipUrlField.setDisable(!studipUrlOtherCheckBox.selectedProperty().get());

		ConfigManager.getStudipOtherUrlEnabledProperty().bind(studipUrlOtherCheckBox.selectedProperty());
		ConfigManager.getUsernameProperty().bind(usernameField.textProperty());
		ConfigManager.getPasswordProperty().bind(passwordField.textProperty());
		ConfigManager.getRememberMeEnabledProperty().bind(rememberMeCheckBox.selectedProperty());

		if (ConfigManager.getRememberMeEnabledProperty().get()) {
			Platform.runLater(() -> {
				try {
					login();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			});
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
			log.debug("Base URL: " + baseUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			studipUrlInfoLabel.setText("Malformed URL");
		}

		studipUrlField.setText(baseUri.toString());

		String username = ConfigManager.getUsernameProperty().getValue();
		String password = ConfigManager.getPasswordProperty().getValue();

		Credentials credentials = new Credentials(username, password);
		log.debug("Base URL: " + studipUrlField.getText());
		log.debug("Credentials: " + credentials);

		SynchronizerApp.studipClient = new StudIPClient(baseUri, credentials);
		SynchronizerApp.studipClient.getCourseService().getDownloadManager()
				.setDownloadDirectory(new File(ConfigManager.getDownloadDirectoryPathProperty().get()));
		AuthService authService = SynchronizerApp.studipClient.getAuthService();

		boolean isSuccessfullyAuthenticated = authService.authenticate();

		if (isSuccessfullyAuthenticated) {
			SynchronizerApp.simpleWindowStage.getController().setStatus("Logged in");

			MainView mainView = new MainView();
			StageController.addRegionToStagingMap(SynchronizerApp.MAIN_STAGE_ID, mainView);

			StageController.setStage(SynchronizerApp.MAIN_STAGE_ID);
			mainView.getSettingsView().getController().loginPerformed();
			// StageController.setStage("SETTINGS_STAGE");
		} else {
			log.error(authService.getAuthErrorResponse());
			SynchronizerApp.simpleWindowStage.getController().setStatus(authService.getAuthErrorResponse());
		}

	}

}
