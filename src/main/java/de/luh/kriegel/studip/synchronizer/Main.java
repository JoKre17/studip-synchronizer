package de.luh.kriegel.studip.synchronizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.client.StudIPService;
import de.luh.kriegel.studip.synchronizer.config.Config;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class);

	public static Config config;

	public static void main(String[] args) throws Exception {

		config = new Config(args);
		log.info(config);

		StudIPService studIPService = new StudIPService(config.baseUri, config.credentials);

		studIPService.authenticate();

		System.out.println(studIPService.getCourses());
	}

}
