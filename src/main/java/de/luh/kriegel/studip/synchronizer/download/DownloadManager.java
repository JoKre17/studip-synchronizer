package de.luh.kriegel.studip.synchronizer.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import de.luh.kriegel.studip.synchronizer.client.BasicHttpClient;
import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;
import de.luh.kriegel.studip.synchronizer.client.service.CourseService;
import de.luh.kriegel.studip.synchronizer.config.Endpoints;
import de.luh.kriegel.studip.synchronizer.config.SubPaths;
import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.data.FileRef;
import de.luh.kriegel.studip.synchronizer.content.model.data.Semester;
import de.luh.kriegel.studip.synchronizer.content.model.file.FileRefNode;
import de.luh.kriegel.studip.synchronizer.content.model.file.FileRefTree;

public class DownloadManager {

	private static final Logger log = LogManager.getLogger(DownloadManager.class);

	private final CourseService courseService;

	private BasicHttpClient httpClient;

	private Path defaultDownloadDirectory;

	private final ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public DownloadManager(CourseService courseService, BasicHttpClient httpClient, Path defaultDownloadDirectory) {
		this.courseService = courseService;
		this.httpClient = httpClient;

		log.debug("Init DownloadManager with defaultDownloadDirectory: " + defaultDownloadDirectory.toAbsolutePath());
		this.defaultDownloadDirectory = defaultDownloadDirectory;
	}

	public void close() {
		log.info("Closing DownloadManager");
		es.shutdown();
	}

	public boolean createDirIfNotExists(File dir) {
		if (dir.exists()) {
			if (dir.isDirectory()) {
				return true;
			} else {
				return false;
			}
		} else {
			return dir.mkdirs();
		}
	}

	private File getDownloadDirectory() {
		return defaultDownloadDirectory.toFile();
	}

	private File getSemesterDirectory(Semester semester) {
		return new File(getDownloadDirectory().getAbsolutePath() + "/" + semester.getTitleAsValidFilename());
	}

	private File getCourseDirectory(Course course) {
		Semester semester;
		try {
			semester = courseService.getSemesterById(course.getStartSemesterId());
		} catch (NotAuthenticatedException | ParseException e) {
			e.printStackTrace();
			return null;
		}

		String courseFilename = course.getTitleAsValidFilename();

		if (course.isTutorium()) {
			courseFilename = courseFilename.replace("Übung", "").trim() + "/Übung";
		} else {
			courseFilename = courseFilename + "/Vorlesung";
		}

		return new File(getSemesterDirectory(semester).getAbsolutePath() + "/" + courseFilename);
	}

	public boolean createDownloadDirectoryIfNotExists() {
		File downloadDir = getDownloadDirectory();

		return createDirIfNotExists(downloadDir);
	}

	public boolean createSemesterDirectoryIfNotExists(Semester semester) {
		File semesterDir = getSemesterDirectory(semester);

		return createDirIfNotExists(semesterDir);
	}

	public boolean createCourseDirectoryIfNotExists(Course course) {
		File courseDir = getCourseDirectory(course);

		return createDirIfNotExists(courseDir);
	}

	private void writeBytesToFile(byte[] bytes, File file) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void downloadFileRefTree(Course course, FileRefTree fileRefTree) {

		if (!createCourseDirectoryIfNotExists(course)) {
			return;
		}

		File parentDir = getCourseDirectory(course);

		Queue<CompletableFuture<Void>> downloadTasks = new ConcurrentLinkedQueue<>();

		downloadFileRefTreeRecursive(parentDir, fileRefTree.getRoot(), downloadTasks);

		int size = fileRefTree.getSize();

		int count = 0;
		for (CompletableFuture<Void> task : downloadTasks) {
			try {
				log.info(count++ + "/" + size + " : " + course.getTitle());
				task.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}
		log.info("DONE : " + course.getTitle());
	}

	private void downloadFileRefTreeRecursive(File parentDir, FileRefNode node,
			Queue<CompletableFuture<Void>> downloadTasks) {

		for (FileRefNode child : node.getChildren()) {
			downloadTasks.add(CompletableFuture.runAsync(() -> {
				if (child.isDirectory()) {
					File dir = new File(parentDir.getAbsolutePath() + "/" + child.getFolder().getNameValidAsFilename());
					createDirIfNotExists(dir);

					downloadFileRefTreeRecursive(dir, child, downloadTasks);
				} else {
					HttpResponse response;

					FileRef fileRef = child.getFileRef();
					File outputFile = new File(parentDir.getAbsolutePath() + "/" + fileRef.getName());

					if (outputFile.exists()) {
						if (fileRef.getChdate() < outputFile.lastModified()) {
							return;
						}
					}

					try {
						response = httpClient.get(SubPaths.API
								+ Endpoints.FILE_DOWNLOAD.getPath().replace(":file_id", fileRef.getId().asHex()));

						BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

						byte[] buffer = new byte[512];

						while (bis.read(buffer) != -1) {
							bos.write(buffer);
						}

						bis.close();
						bos.close();

						log.debug("Downloaded " + child.getFileRef().getName() + " " + (outputFile.length() / 1048576f)
								+ " MB");

					} catch (URISyntaxException | IOException e) {
						e.printStackTrace();
					}
				}
			}, es));

		}

	}

}
