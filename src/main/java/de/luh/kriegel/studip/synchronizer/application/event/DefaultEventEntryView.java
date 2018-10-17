package de.luh.kriegel.studip.synchronizer.application.event;

import java.io.IOException;

import de.luh.kriegel.studip.synchronizer.event.Event;
import de.luh.kriegel.studip.synchronizer.event.EventView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

public class DefaultEventEntryView implements EventView {

	@FXML
	private WebView webView;
	
	@FXML
	private Label eventTitleLabel;
	
	@FXML
	private Label eventContentLabel;
	
	private Node contentNode;
	private Event event;
	
	public DefaultEventEntryView(Event event, String eventTitle, String eventContent) {
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DefaultEventEntry.fxml"));
		loader.setController(this);

		try {
			contentNode = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		eventTitleLabel.setText(eventTitle);
		eventContentLabel.setText(eventContent);
		
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
