package de.kriegel.studip.synchronizer.application.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kriegel.studip.synchronizer.application.SimpleWindowStage;
import de.kriegel.studip.synchronizer.application.config.ConfigManager;
import javafx.scene.layout.Region;

public class StageController {

	private static final Logger log = LoggerFactory.getLogger(StageController.class);

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
		log.info("Registered stage: " + stageId);
	}

	public static void setStage(String stageId) {
		if (simpleWindowStage == null) {
			log.error("simpleWindowStage is not initialized");
		}

		if (ConfigManager.getMinimizedStartOfApplicationProperty().get()) {
			simpleWindowStage.getController().hide(simpleWindowStage);
		}

		log.info("Change stage to: " + stageId);
		Region region = stagingMap.get(stageId);
		simpleWindowStage.getController().setContent(region);

		simpleWindowStage.show();
	}
	
	public static Region getStageById(String stageId) {
		return stagingMap.get(stageId);
	}
	
}
