package de.luh.kriegel.studip.synchronizer.application.event;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.luh.kriegel.studip.client.content.model.data.Course;
import de.luh.kriegel.studip.client.event.Event;
import de.luh.kriegel.studip.client.event.EventView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CourseDownloadFinishedEventView implements EventView {

	@FXML
	private StackPane heightLimitingStackPane;

	@FXML
	private VBox seeMoreVBox;

	@FXML
	private HBox seeMoreHBox;
	
	@FXML
	private Button seeMoreButton;

	@FXML
	private Label courseLabel;

	@FXML
	private GridPane downloadedFilesGridPane;

	private Course course;
	private List<File> downloadedFiles;
	
	private Node contentNode;
	private Event event;

	public CourseDownloadFinishedEventView(Event event, Course course,  List<File> downloadedFiles) {
		this.event = event;
		this.course = course;
		this.downloadedFiles = downloadedFiles;

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DownloadEventEntry.fxml"));
		loader.setController(this);

		Pane pane;
		
		try {
			pane = loader.load();
			contentNode = pane;

			seeMoreVBox.prefWidthProperty().bind(pane.widthProperty());
			seeMoreHBox.prefHeightProperty().bind(pane.heightProperty());
		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public Course getCourse() {
		return course;
	}
	
	public List<File> getDownloadedFiles() {
		return downloadedFiles;
	}
	
	public Node getContentNode() {
		return contentNode;
	}

	@Override
	public Node getEventNode() {
		return contentNode;
	}

	@Override
	public Event getEvent() {
		return event;
	}
	
	

}
