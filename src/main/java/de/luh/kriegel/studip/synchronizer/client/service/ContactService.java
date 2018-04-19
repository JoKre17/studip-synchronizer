package de.luh.kriegel.studip.synchronizer.client.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.client.BasicHttpClient;

public class ContactService {

	private static final Logger log = LogManager.getLogger(ContactService.class);
	
	private final BasicHttpClient httpClient;

	public ContactService(BasicHttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
