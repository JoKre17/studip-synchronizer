package de.luh.kriegel.studip.synchronizer.event;

import org.json.simple.JSONObject;

import de.luh.kriegel.studip.synchronizer.client.exception.NotAuthenticatedException;

public abstract class EventBuilder {
		
	public abstract Event fromJson(JSONObject json) throws NotAuthenticatedException;
		
}
