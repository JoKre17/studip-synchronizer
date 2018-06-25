package de.luh.kriegel.studip.synchronizer;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.SynchronizerApp;
import de.luh.kriegel.studip.synchronizer.auth.Credentials;
import de.luh.kriegel.studip.synchronizer.client.StudIPClient;
import de.luh.kriegel.studip.synchronizer.client.service.AuthService;
import de.luh.kriegel.studip.synchronizer.client.service.CourseService;
import de.luh.kriegel.studip.synchronizer.config.Config;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.file.FileRefTree;
import de.luh.kriegel.studip.synchronizer.download.DownloadManager;
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
		
		if(config.baseUri == null) {
			config.baseUri = new URI("https://studip.uni-hannover.de");
		}
		
		if(config.credentials == null) {
			config.credentials = new Credentials("JK_14", "Aiedail95");
		}

		StudIPClient studIPClient = new StudIPClient(config.baseUri, config.credentials);

		AuthService authService = studIPClient.getAuthService();
		authService.authenticate();

		CourseService courseService = studIPClient.getCourseService();
		DownloadManager downloadManager = courseService.getDownloadManager();

		Map<Course, Course> courseTutorialMap = courseService.getCourseTutorialMap();

		List<CompletableFuture<Void>> downloadTasks = new ArrayList<>();
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for (Entry<Course, Course> e : courseTutorialMap.entrySet()) {

			Course lecture = e.getKey();
			Course tutorial = e.getValue();

			downloadTasks.add(CompletableFuture.runAsync(() -> {
				FileRefTree fileRefTree;

				try {
					fileRefTree = courseService.getFileRefTree(lecture);
					downloadManager.downloadFileRefTree(lecture, fileRefTree, null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				if (tutorial != null) {
					try {
						fileRefTree = courseService.getFileRefTree(lecture);
						downloadManager.downloadFileRefTree(tutorial, fileRefTree, null);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}, es));

		}

		downloadTasks.forEach(t -> {
			try {
				t.get();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		courseService.close();
		es.shutdown();

		// Course course = courseTutorialMap.entrySet().iterator().next().getKey();
		// FileRefTree fileRefTree = courseService.getFileRefTree(course);
		// downloadManager.downloadFileRefTree(course, fileRefTree);

	}

}
