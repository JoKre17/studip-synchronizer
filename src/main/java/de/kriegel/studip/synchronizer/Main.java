package de.kriegel.studip.synchronizer;

import java.io.File;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kriegel.studip.client.auth.Credentials;
import de.kriegel.studip.client.config.Config;
import de.kriegel.studip.client.download.DownloadManager;
import de.kriegel.studip.client.service.AuthService;
import de.kriegel.studip.client.service.CourseService;
import de.kriegel.studip.client.service.StudIPClient;
import de.kriegel.studip.synchronizer.application.SynchronizerApp;
import javafx.application.Application;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class);

	public static Config config;

	public static void main(String[] args) throws Exception {

		config = new Config(args);
		log.info(config);

		log.info(new File(".").getAbsolutePath());

		Thread applicationThread = new Thread() {
			@Override
			public void run() {
				Application.launch(SynchronizerApp.class);
			}
		};
		applicationThread.start();

		if (config.baseUri == null) {
			config.baseUri = new URI("https://studip.uni-hannover.de");
		}

		if (config.credentials == null) {
			config.credentials = new Credentials("JK_14", "Aiedail95");
		}

		StudIPClient studIPClient = new StudIPClient(config.baseUri, config.credentials);

		AuthService authService = studIPClient.getAuthService();
		authService.authenticate();

		CourseService courseService = studIPClient.getCourseService();
		DownloadManager downloadManager = courseService.getDownloadManager();

	}

}
