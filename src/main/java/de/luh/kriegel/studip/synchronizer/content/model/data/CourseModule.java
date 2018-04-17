package de.luh.kriegel.studip.synchronizer.content.model.data;

import de.luh.kriegel.studip.synchronizer.content.util.RegexHelper;

public class CourseModule {

	private final Id courseId;
	private final CourseModuleType moduleType;
	
	private final String subUrl;
	
	public CourseModule(Id courseId, CourseModuleType moduleType, String subUrl) {
		this.courseId = courseId;
		this.moduleType = moduleType;
		
		this.subUrl = subUrl;
	}

	public static CourseModule fromJson(String moduleKey, String link) {
		assert moduleKey != null;
		assert link != null;
		
		Id courseId = RegexHelper.extractIdFromString(link);
		CourseModuleType moduleType = CourseModuleType.valueOf(moduleKey.toUpperCase());
		
		return new CourseModule(courseId, moduleType, link);
	}

	public Id getCourseId() {
		return courseId;
	}

	public CourseModuleType getModuleType() {
		return moduleType;
	}
	
	public String getSubUrl() {
		return subUrl;
	}
}
