package de.kriegel.studip.synchronizer.application.event;

import de.kriegel.studip.client.event.Event;
import javafx.scene.Node;

public interface EventView {

	public Node getEventNode();
	
	public Event getEvent();
	
}
