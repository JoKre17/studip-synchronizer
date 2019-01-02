package de.kriegel.studip.synchronizer.application.model;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.Pair;

public class CustomServerPair extends Pair<String, URL> {
	
	private static final long serialVersionUID = -4989185744707833133L;
	private static final Logger log = LoggerFactory.getLogger(CustomServerPair.class);
	
	public CustomServerPair(String serverName, URL url) {
		super(serverName, url);
	}

	@Override
	public String toString() {
		return getKey();
	}
	
}
