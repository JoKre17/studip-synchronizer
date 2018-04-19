package de.luh.kriegel.studip.synchronizer.client.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.client.BasicHttpClient;

public class ForumService {

	private static final Logger log = LogManager.getLogger(ForumService.class);
	
	private final BasicHttpClient httpClient;

	public ForumService(BasicHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
}
