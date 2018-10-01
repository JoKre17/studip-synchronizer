package de.luh.kriegel.studip.synchronizer.application.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.download.SynchronizeTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ConfigManager {

	private static final Logger log = LogManager.getLogger(ConfigManager.class);

	private static final String PROPERTIES_FILE_PATH = "settings.conf";
	private static final Properties properties = new Properties();

	// LOGIN
	private static final String STUDIP_SERVER_NAME_KEY = "STUDIP_SERVER_NAME";
	private static final String STUDIP_OTHER_URL_ENABLED_KEY = "STUDIP_OTHER_URL_ENABLED";
	private static final String STUDIP_URL_KEY = "STUDIP_URL";
	private static final String USERNAME_KEY = "USERNAME";
	private static final String PASSWORD_KEY = "PASSWORD";
	private static final String REMEMBER_ME_ENABLED_KEY = "REMEMBER_ME_ENABLED";

	private static final StringProperty studipServerNameProperty = new SimpleStringProperty("");
	private static final BooleanProperty studipOtherUrlEnabledProperty = new SimpleBooleanProperty(true);
	private static final StringProperty studipUrlProperty = new SimpleStringProperty("");
	private static final StringProperty usernameProperty = new SimpleStringProperty("");
	private static final StringProperty passwordProperty = new SimpleStringProperty("");
	private static final BooleanProperty rememberMeEnabledProperty = new SimpleBooleanProperty(false);

	// DOWNLOAD
	private static final String DOWNLOAD_ENABLED_KEY = "DOWNLOAD_ENABLED";
	private static final String SYNCHRONIZATION_INTERVAL_KEY = "SYNCHRONIZATION_INTERVAL";
	private static final String DOWNLOAD_DIRECTORY_PATH_KEY = "DOWNLOAD_DIRECTORY_PATH";

	private static final BooleanProperty downloadEnabledProperty = new SimpleBooleanProperty(true);
	private static final IntegerProperty synchronizationIntervalProperty = new SimpleIntegerProperty(5);
	private static final StringProperty downloadDirectoryPathProperty = new SimpleStringProperty("");

	// NOTIFICATIONS
	private static final String NOTIFICATIONS_ENABLED_KEY = "NOTIFICATIONS_ENABLED";

	private static final BooleanProperty notificationsEnabledProperty = new SimpleBooleanProperty(true);

	// APPLICATION
	private static final String FIRST_START_OF_APPLICATION_KEY = "FIRST_START";
	private static final String MINIMIZED_START_OF_APPLICATION_KEY = "MINIMIZED_START";
	private static final String RUN_APPLICATION_ON_SYSTEM_START_KEY = "RUN_APPLICATION_ON_SYSTEM_START";

	private static final BooleanProperty firstStartOfApplicationProperty = new SimpleBooleanProperty(true);
	private static final BooleanProperty minimizedStartOfApplicationProperty = new SimpleBooleanProperty(false);
	private static final BooleanProperty runApplicationOnSystemStartProperty = new SimpleBooleanProperty(false);

	public ConfigManager() {

		try {
			loadProperties();
			setListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadProperties() throws Exception {
		File configFile = new File(PROPERTIES_FILE_PATH).getAbsoluteFile();

		if (!configFile.exists()) {
			boolean createDirsSuccess = false;
			if (configFile.getParentFile().exists() && configFile.getParentFile().isDirectory()) {
				createDirsSuccess = true;
			} else {
				configFile.getParentFile().mkdirs();
			}

			boolean createFileSuccess = false;
			if (createDirsSuccess) {
				createFileSuccess = configFile.createNewFile();
			}

			if (!createDirsSuccess || !createFileSuccess) {
				throw new Exception(createDirsSuccess + " " + createFileSuccess + " Could not create Settings file!");
			}
		}

		Reader reader = new InputStreamReader(new FileInputStream(configFile), "UTF-8");

		if (reader != null) {
			properties.load(reader);
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			switch (key) {
			// LOGIN
			case STUDIP_SERVER_NAME_KEY:
				studipServerNameProperty.set(value);
				break;
			case STUDIP_OTHER_URL_ENABLED_KEY:
				studipOtherUrlEnabledProperty.set(Boolean.parseBoolean(value));
				break;
			case STUDIP_URL_KEY:
				studipUrlProperty.set(value);
				break;
			case USERNAME_KEY:
				usernameProperty.set(value);
				break;
			case PASSWORD_KEY:
				passwordProperty.set(value);
				break;
			case REMEMBER_ME_ENABLED_KEY:
				rememberMeEnabledProperty.set(Boolean.parseBoolean(value));
				break;

			// DOWNLOAD
			case DOWNLOAD_ENABLED_KEY:
				downloadEnabledProperty.set(Boolean.parseBoolean(value));
				break;
			case SYNCHRONIZATION_INTERVAL_KEY:
				synchronizationIntervalProperty.set(Integer.parseInt(value));
				break;
			case DOWNLOAD_DIRECTORY_PATH_KEY:
				downloadDirectoryPathProperty.set(value);
				break;

			// NOTIFICATION
			case NOTIFICATIONS_ENABLED_KEY:
				notificationsEnabledProperty.set(Boolean.parseBoolean(value));
				break;

			// APPLICATION
			case FIRST_START_OF_APPLICATION_KEY:
				firstStartOfApplicationProperty.set(Boolean.parseBoolean(value));
				break;
			case MINIMIZED_START_OF_APPLICATION_KEY:
				minimizedStartOfApplicationProperty.set(Boolean.parseBoolean(value));
				break;
			case RUN_APPLICATION_ON_SYSTEM_START_KEY:
				runApplicationOnSystemStartProperty.set(Boolean.parseBoolean(value));
				break;
			}
		}
	}

	private void saveProperties() throws Exception {
		File configFile = new File(PROPERTIES_FILE_PATH);
		if (!configFile.exists()) {
			boolean createDirsSuccess = configFile.mkdirs();
			boolean createFileSuccess = false;
			if (createDirsSuccess) {
				createFileSuccess = configFile.createNewFile();
			}
			if (!createDirsSuccess || !createFileSuccess) {
				throw new Exception("Could not create Settings file!");
			}
		}

		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));

		if (pw != null) {
			properties.store(pw, "");
		}
	}

	private void setListeners() {
		// LOGIN
		studipServerNameProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				properties.setProperty(STUDIP_SERVER_NAME_KEY, newValue);
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		studipOtherUrlEnabledProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(STUDIP_OTHER_URL_ENABLED_KEY, newValue.toString());
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		studipUrlProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				properties.setProperty(STUDIP_URL_KEY, newValue);
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		usernameProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (rememberMeEnabledProperty.get()) {
					properties.setProperty(USERNAME_KEY, newValue);
					try {
						saveProperties();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		passwordProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (rememberMeEnabledProperty.get()) {
					properties.setProperty(PASSWORD_KEY, newValue);
					try {
						saveProperties();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		rememberMeEnabledProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(REMEMBER_ME_ENABLED_KEY, newValue.toString());

				if (newValue) {
					properties.setProperty(USERNAME_KEY, usernameProperty.get());
					properties.setProperty(PASSWORD_KEY, passwordProperty.get());
				} else {
					properties.remove(USERNAME_KEY);
					properties.remove(PASSWORD_KEY);
				}

				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// DOWNLOAD
		downloadEnabledProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(DOWNLOAD_ENABLED_KEY, newValue.toString());

				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		synchronizationIntervalProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				properties.setProperty(SYNCHRONIZATION_INTERVAL_KEY, newValue.toString());

				if (downloadEnabledProperty.get()) {
					if (SynchronizerApp.synchronizerTimer != null) {
						SynchronizerApp.synchronizerTimer.interrupt();
					}

					File downloadDirectory = new File(downloadDirectoryPathProperty.get());

					if (downloadDirectory.exists() && downloadDirectory.isDirectory()) {
						SynchronizerApp.synchronizerTimer = new SynchronizeTimer(SynchronizerApp.studipClient,
								synchronizationIntervalProperty.get() * 60000);
						SynchronizerApp.synchronizerTimer.start();
					} else {
						log.warn("Could not start Synchronization. Conditions:");
						log.warn("\tdownload directory exists: " + downloadDirectory.exists());
						log.warn("\tdownload directory is directory: " + downloadDirectory.isDirectory());
						properties.setProperty(SYNCHRONIZATION_INTERVAL_KEY, oldValue.toString());
					}
				}

				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		downloadDirectoryPathProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				properties.setProperty(DOWNLOAD_DIRECTORY_PATH_KEY, newValue);

				SynchronizerApp.studipClient.getCourseService().getDownloadManager()
						.setDownloadDirectory(new File(newValue));
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// NOTIFICATION
		notificationsEnabledProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(NOTIFICATIONS_ENABLED_KEY, newValue.toString());
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// APPLICATION
		firstStartOfApplicationProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(FIRST_START_OF_APPLICATION_KEY, newValue.toString());
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		minimizedStartOfApplicationProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(MINIMIZED_START_OF_APPLICATION_KEY, newValue.toString());
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		runApplicationOnSystemStartProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				properties.setProperty(RUN_APPLICATION_ON_SYSTEM_START_KEY, newValue.toString());
				try {
					saveProperties();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	// LOGIN

	public static StringProperty getStudipServerNameProperty() {
		return studipServerNameProperty;
	}

	public static BooleanProperty getStudipOtherUrlEnabledProperty() {
		return studipOtherUrlEnabledProperty;
	}

	public static StringProperty getStudipUrlProperty() {
		return studipUrlProperty;
	}

	public static StringProperty getUsernameProperty() {
		return usernameProperty;
	}

	public static StringProperty getPasswordProperty() {
		return passwordProperty;
	}

	public static BooleanProperty getRememberMeEnabledProperty() {
		return rememberMeEnabledProperty;
	}

	// DOWNLOAD

	public static BooleanProperty getDownloadEnabledProperty() {
		return downloadEnabledProperty;
	}

	public static IntegerProperty getSynchronizationIntervalProperty() {
		return synchronizationIntervalProperty;
	}

	public static StringProperty getDownloadDirectoryPathProperty() {
		return downloadDirectoryPathProperty;
	}

	// NOTIFICATIONS

	public static BooleanProperty getNotificationsEnabledProperty() {
		return notificationsEnabledProperty;
	}

	// APPLICATION

	public static BooleanProperty getFirstStartOfApplicationProperty() {
		return firstStartOfApplicationProperty;
	}

	public static BooleanProperty getMinimizedStartOfApplicationProperty() {
		return minimizedStartOfApplicationProperty;
	}

	public static BooleanProperty getRunApplicationOnSystemStartProperty() {
		return runApplicationOnSystemStartProperty;
	}
}
