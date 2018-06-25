package de.luh.kriegel.studip.synchronizer.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.client.StudIPClient;
import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.client.service.CourseService;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.file.FileRefTree;

public class SynchronizeTimer extends Thread {

	private static final Logger log = LogManager.getLogger(SynchronizeTimer.class);

	private StudIPClient studipClient;
	private long sleepTimeMillis = 0;

	private CourseService courseService;
	private DownloadManager downloadManager;
	private Map<Course, Course> courseTutorialMap;

	private AtomicBoolean cancelled = new AtomicBoolean(false);

	public SynchronizeTimer(StudIPClient studipClient, long sleepTimeMillis) {
		this.studipClient = studipClient;
		assert sleepTimeMillis > 5000;

		this.sleepTimeMillis = sleepTimeMillis;

		try {
			init();
		} catch (NotAuthenticatedException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void init() throws NotAuthenticatedException, ParseException {
		courseService = studipClient.getCourseService();
		downloadManager = courseService.getDownloadManager();

		courseTutorialMap = courseService.getCourseTutorialMap();
	}

	@Override
	public void run() {
		
		if(!downloadManager.getDownloadDirectory().exists() || !downloadManager.getDownloadDirectory().isDirectory()) {
			log.info(downloadManager.getDownloadDirectory().getAbsolutePath() + " does not exist or is no directory!");
			return;
		}
		
		List<CompletableFuture<Void>> downloadTasks = new ArrayList<>();
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try {
			while (true) {
				for (Entry<Course, Course> e : courseTutorialMap.entrySet()) {

					Course lecture = e.getKey();
					Course tutorial = e.getValue();

					downloadTasks.add(CompletableFuture.runAsync(() -> {
						FileRefTree fileRefTree;

						try {
							fileRefTree = courseService.getFileRefTree(lecture);
							downloadManager.downloadFileRefTree(lecture, fileRefTree, cancelled);

							if (tutorial != null) {
								fileRefTree = courseService.getFileRefTree(lecture);
								downloadManager.downloadFileRefTree(tutorial, fileRefTree, cancelled);
							}

						} catch (Exception e1) {
							System.out.println("Inner exception");
							e1.printStackTrace();
						}
					}, es));

				}

				log.info("Created " + downloadTasks.size() + " Download tasks.");

				for (int i = 0; i < downloadTasks.size(); i++) {
					if (cancelled.get()) {
						downloadTasks.get(i).cancel(true);
						continue;
					}

					try {
						downloadTasks.get(i).get();
					} catch (InterruptedException | ExecutionException e1) {
						cancelled.set(true);
						log.info("INITIAL interrupt at task " + i);
					}
				}

				log.debug("Sleep Time! for (" + sleepTimeMillis + ")");
				Thread.sleep(sleepTimeMillis);
			}
		} catch (InterruptedException e) {
			
		} finally {
			es.shutdown();
		}
	}

}
