package de.luh.kriegel.studip.synchronizer.content.model.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CourseNews {

	private final Id id;
	private final String topic;
	private final String body;
	private final long date;
	private final Id user_id;
	private final long expire;
	private final int allow_comments;
	private final long chdate;
	private final Id chdate_uid;
	private final long mkdate;
	private final String body_html;
	private final List<String> ranges;

	public CourseNews(Id id, String topic, String body, long date, Id user_id, long expire, int allow_comments,
			long chdate, Id chdate_uid, long mkdate, String body_html, List<String> ranges) {
		super();
		this.id = id;
		this.topic = topic;
		this.body = body;
		this.date = date;
		this.user_id = user_id;
		this.expire = expire;
		this.allow_comments = allow_comments;
		this.chdate = chdate;
		this.chdate_uid = chdate_uid;
		this.mkdate = mkdate;
		this.body_html = body_html;
		this.ranges = ranges;
	}

	@SuppressWarnings("unchecked")
	public static CourseNews fromJson(JSONObject jsonObject) {
		assert jsonObject != null;
		assert jsonObject.containsKey("news_id");

		Id id = null;
		String topic = "";
		String body = "";
		long date = 0;
		Id user_id = null;
		long expire = 0;
		int allow_comments = 0;
		long chdate = 0;
		Id chdate_uid = null;
		long mkdate = 0;
		String body_html = "";
		List<String> ranges = new ArrayList<>();

		if (jsonObject.containsKey("news_id")) {
			id = new Id(jsonObject.get("news_id").toString());
		}

		if (jsonObject.containsKey("topic")) {
			topic = jsonObject.get("topic").toString();
		}

		if (jsonObject.containsKey("body")) {
			body = jsonObject.get("body").toString();
		}

		if (jsonObject.containsKey("date")) {
			date = Long.parseLong(jsonObject.get("date").toString());
		}

		if (jsonObject.containsKey("user_id")) {
			user_id = new Id(jsonObject.get("user_id").toString());
		}

		if (jsonObject.containsKey("expire")) {
			expire = Long.parseLong(jsonObject.get("expire").toString());
		}

		if (jsonObject.containsKey("allow_comments")) {
			allow_comments = Integer.parseInt(jsonObject.get("allow_comments").toString());
		}

		if (jsonObject.containsKey("chdate")) {
			chdate = Long.parseLong(jsonObject.get("chdate").toString());
		}

		if (jsonObject.containsKey("chdate_uid")) {
			if (!jsonObject.get("chdate_uid").equals("")) {
				chdate_uid = new Id(jsonObject.get("chdate_uid").toString());
			}
		}

		if (jsonObject.containsKey("mkdate")) {
			mkdate = Long.parseLong(jsonObject.get("mkdate").toString());
		}

		if (jsonObject.containsKey("body_html")) {
			body_html = jsonObject.get("body_html").toString();
		}

		if (jsonObject.containsKey("ranges")) {

			JSONArray rangesJson = (JSONArray) jsonObject.get("ranges");
			rangesJson.forEach(range -> ranges.add(range.toString()));
		}

		return new CourseNews(id, topic, body, date, user_id, expire, allow_comments, chdate, chdate_uid, mkdate,
				body_html, ranges);
	}

	public Id getId() {
		return id;
	}

	public String getTopic() {
		return topic;
	}

	public String getBody() {
		return body;
	}

	public long getDate() {
		return date;
	}

	public Id getUser_id() {
		return user_id;
	}

	public long getExpire() {
		return expire;
	}

	public int getAllow_comments() {
		return allow_comments;
	}

	public long getChdate() {
		return chdate;
	}

	public Id getChdate_uid() {
		return chdate_uid;
	}

	public long getMkdate() {
		return mkdate;
	}

	public String getBody_html() {
		return body_html;
	}

	public List<String> getRanges() {
		return ranges;
	}

	@Override
	public String toString() {
		return id.toString() + " - " + getTopic();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Course)) {
			return false;
		}
		Course other = (Course) obj;
		if (id == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!id.equals(other.getId())) {
			return false;
		}
		return true;
	}

}
