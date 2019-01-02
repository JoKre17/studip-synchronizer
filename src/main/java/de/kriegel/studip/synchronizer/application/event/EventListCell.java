package de.kriegel.studip.synchronizer.application.event;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class EventListCell extends ListCell<EventView> {
	
	private final SimpleDateFormat eventDateFormatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
	
	@FXML
	private Label typeLabel;
	
	@FXML
	private AnchorPane viewAnchorPane;

	@FXML
	private Label dateLabel;

	@FXML
	private GridPane downloadedFilesGridPane;
	
	private ListView<EventView> eventListView;
	
	public EventListCell(ListView<EventView> eventListView) {
		this.eventListView = eventListView;
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/EventListCell.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
    protected void updateItem(EventView item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
        	
        	typeLabel.setText(item.getEvent().getEventType().getSimpleName());
			
        	viewAnchorPane.getChildren().clear();
        	viewAnchorPane.getChildren().add(item.getEventNode());
        	
        	dateLabel.setText(eventDateFormatter.format(item.getEvent().getEventDate()));
			
        	if(item.getEventNode() instanceof AnchorPane) {
        		AnchorPane eventAnchorPane = (AnchorPane) item.getEventNode();
        		
        		eventAnchorPane.prefWidthProperty().bind(viewAnchorPane.widthProperty());
        		eventAnchorPane.maxWidthProperty().bind(viewAnchorPane.maxWidthProperty());
        	}

        	setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        	
        	layout();
        }
    }
	
}
