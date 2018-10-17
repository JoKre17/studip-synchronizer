package de.luh.kriegel.studip.synchronizer.application.event;

import java.io.IOException;

import de.luh.kriegel.studip.synchronizer.content.model.data.Course;
import de.luh.kriegel.studip.synchronizer.content.model.file.FileRefTree;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class DownloadCompletedEventEntry {

	@FXML
	StackPane heightLimitingStackPane;

	@FXML
	Button seeMoreButton;

	@FXML
	Label courseLabel;

	@FXML
	GridPane downloadedFilesGridPane;

	private Course course;
	private FileRefTree fileRefTree;

	public DownloadCompletedEventEntry(Course course, FileRefTree fileRefTree) {
		this.course = course;
		this.fileRefTree = fileRefTree;

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DownloadEventEntry.fxml"));
		loader.setController(this);

		try {
			// Pane root = loader.load();
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	

}
