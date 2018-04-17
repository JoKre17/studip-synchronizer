package de.luh.kriegel.studip.synchronizer.content.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import de.luh.kriegel.studip.synchronizer.content.util.RegexHelper;

public class Course {

	private final Id id;
	private final int number;
	private final String title;
	private final String subtitle;
	private final int type;
	private final String description;
	private final String location;
	private final List<User> lecturers;
	private final Map<CourseMemberType, Integer> memberCounts;
	private final Id start_semesterId;
	private final Id end_semesterId;
	private final List<CourseModule> modules;
	private final int group;

	public Course(Id id, int number, String title, String subtitle, int type, String description, String location,
			List<User> lecturers, Map<CourseMemberType, Integer> memberCounts, Id start_semesterId, Id end_semesterId,
			List<CourseModule> modules, int group) {
		this.id = id;
		this.number = number;
		this.title = title;
		this.subtitle = subtitle;
		this.type = type;
		this.description = description;
		this.location = location;
		this.lecturers = lecturers;
		this.memberCounts = memberCounts;
		this.start_semesterId = start_semesterId;
		this.end_semesterId = end_semesterId;
		this.modules = modules;
		this.group = group;
	}

	@SuppressWarnings("unchecked")
	public static Course fromJson(JSONObject jsonObject) {
		assert jsonObject != null;
		assert jsonObject.containsKey("course_id");

		Id id = null;
		int number = 0;
		String title = "";
		String subtitle = "";
		int type = 0;
		String description = "";
		String location = "";
		List<User> lecturers = new ArrayList<>();
		Map<CourseMemberType, Integer> memberCounts = new HashMap<>();
		Id start_semesterId = null;
		Id end_semesterId = null;
		List<CourseModule> modules = new ArrayList<>();
		int group = 0;

		if (jsonObject.containsKey("course_id")) {
			id = new Id(jsonObject.get("course_id").toString());
		}

		if (jsonObject.containsKey("number")) {
			if(jsonObject.get("number").toString().isEmpty()) {
				number = 0;
			} else {
				number = Integer.parseInt(jsonObject.get("number").toString().trim());
			}
		}

		if (jsonObject.containsKey("title")) {
			title = jsonObject.get("title").toString().trim();
		}

		if (jsonObject.containsKey("subtitle")) {
			subtitle = jsonObject.get("subtitle").toString().trim();
		}

		if (jsonObject.containsKey("type")) {
			type = Integer.parseInt(jsonObject.get("type").toString());
		}

		if (jsonObject.containsKey("description")) {
			description = jsonObject.get("description").toString().trim();
		}

		if (jsonObject.containsKey("location")) {
			location = jsonObject.get("location").toString().trim();
		}

		if (jsonObject.containsKey("lecturers")) {
			
			JSONObject lecturersJson = (JSONObject) jsonObject.get("lecturers");
			((Map<String, JSONObject>) lecturersJson).forEach((key, value) -> {
				User user = User.fromJson(value);
				lecturers.add(user);
			});
		}

		if (jsonObject.containsKey("members")) {

			((Map<String, Object>) jsonObject.get("members")).forEach((key, value) -> {
				if (key.contains("count")) {
					String memberTypeIdentifier = key.split("_")[0].toUpperCase();
					CourseMemberType memberType = CourseMemberType.valueOf(memberTypeIdentifier);

					Integer count = ((Long) value).intValue();

					memberCounts.put(memberType, count);
				}
			});
		}

		if (jsonObject.containsKey("start_semester")) {
			start_semesterId = RegexHelper.extractIdFromString(jsonObject.get("start_semester").toString());
		}

		if (jsonObject.containsKey("end_semester")) {
			end_semesterId = RegexHelper.extractIdFromString(jsonObject.get("end_semester").toString());
		}

		if (jsonObject.containsKey("modules")) {

			((Map<String, String>) jsonObject.get("modules")).forEach((moduleKey, link) -> {
				CourseModule module = CourseModule.fromJson(moduleKey, link);

				modules.add(module);
			});
		}

		if (jsonObject.containsKey("group")) {
			group = Integer.parseInt(jsonObject.get("group").toString());
		}

		return new Course(id, number, title, subtitle, type, description, location, lecturers, memberCounts,
				start_semesterId, end_semesterId, modules, group);

	}

	public Id getId() {
		return id;
	}

	public int getNumber() {
		return number;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public int getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public List<User> getLecturers() {
		return lecturers;
	}

	public Map<CourseMemberType, Integer> getMemberCounts() {
		return memberCounts;
	}

	public Id getStart_semesterId() {
		return start_semesterId;
	}

	public Id getEnd_semesterId() {
		return end_semesterId;
	}

	public List<CourseModule> getModules() {
		return modules;
	}

	public int getGroup() {
		return group;
	}
	
	@Override
	public String toString() {
		return id.toString() + " - " + title;
	}

}
