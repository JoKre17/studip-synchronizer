package de.luh.kriegel.studip.synchronizer.application.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.luh.kriegel.studip.synchronizer.application.SimpleWindowStage;
import de.luh.kriegel.studip.synchronizer.application.config.ConfigManager;
import javafx.scene.layout.Region;

public class StageController {
	
	private static final Logger log = LogManager.getLogger(StageController.class);

	private static SimpleWindowStage simpleWindowStage;
	
	private static final Map<String, Region> stagingMap = new HashMap<>();
	
	public static void setSimpleWindowStage(SimpleWindowStage simpleWindowStage) {
		assert simpleWindowStage != null;
		
		StageController.simpleWindowStage = simpleWindowStage;
	}
	
	public static void addRegionToStagingMap(String stageId, Region region) {
		assert stageId != null;
		assert region != null;
		
		stagingMap.put(stageId, region);
		log.debug("Registered stage: " + stageId);
	}
	
	public static void setStage(String stageId) {
		if(simpleWindowStage == null) {
			log.error("simpleWindowStage is not initialized");
		}
		
		if(ConfigManager.getMinimizedStartOfApplicationProperty().get()) {
			simpleWindowStage.getController().hide(simpleWindowStage);
		}
		
		log.debug("Change stage to: " + stageId);
		Region region = stagingMap.get(stageId);
		simpleWindowStage.getController().setContent(region);
		
		simpleWindowStage.show();
	}
	
	
	
}
