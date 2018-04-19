package de.luh.kriegel.studip.synchronizer.content.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class Folder {

	private final boolean is_visible;
	private final boolean is_readable;
	private final boolean is_writable;
	private final Id id;
	private final String user_id;
	private final String parent_id;
	private final Id range_id;
	private final String range_type;
	private final String folder_type;
	private final String name;
	private final List<Id> data_content;
	private final String description;
	private final long mkdate;
	private final long chdate;
	private final List<Id> subfolders;
	private final List<Id> file_refs;

	public Folder(boolean is_visible, boolean is_readable, boolean is_writable, Id id, String user_id, String parent_id,
			Id range_id, String range_type, String folder_type, String name, List<Id> data_content, String description,
			long mkdate, long chdate, List<Id> subfolders, List<Id> file_refs) {
		this.is_visible = is_visible;
		this.is_readable = is_readable;
		this.is_writable = is_writable;
		this.id = id;
		this.user_id = user_id;
		this.parent_id = parent_id;
		this.range_id = range_id;
		this.range_type = range_type;
		this.folder_type = folder_type;
		this.name = name;
		this.data_content = data_content;
		this.description = description;
		this.mkdate = mkdate;
		this.chdate = chdate;
		this.subfolders = subfolders;
		this.file_refs = file_refs;
	}

	@SuppressWarnings("unchecked")
	public static Folder fromJson(JSONObject jsonObject) {
		assert jsonObject != null;
		assert jsonObject.containsKey("id");

		boolean is_visible = false;
		boolean is_readable = false;
		boolean is_writable = false;
		Id id = null;
		String user_id = "";
		String parent_id = "";
		Id range_id = null;
		String range_type = "";
		String folder_type = "";
		String name = "";
		List<Id> data_content = new ArrayList<>();
		String description = "";
		long mkdate = 0;
		long chdate = 0;
		List<Id> subfolders = new ArrayList<>();
		List<Id> file_refs = new ArrayList<>();

		if (jsonObject.containsKey("is_visible")) {
			is_visible = Boolean.parseBoolean(jsonObject.get("is_visible").toString());
		}

		if (jsonObject.containsKey("is_readable")) {
			is_readable = Boolean.parseBoolean(jsonObject.get("is_readable").toString());
		}

		if (jsonObject.containsKey("is_writable")) {
			is_writable = Boolean.parseBoolean(jsonObject.get("is_writable").toString());
		}

		if (jsonObject.containsKey("id")) {
			id = new Id(jsonObject.get("id").toString());
		}

		if (jsonObject.containsKey("user_id")) {
			user_id = jsonObject.get("user_id").toString();
		}

		if (jsonObject.containsKey("parent_id")) {
			parent_id = jsonObject.get("parent_id").toString();
		}

		if (jsonObject.containsKey("range_id")) {
			range_id = new Id(jsonObject.get("range_id").toString());
		}

		if (jsonObject.containsKey("range_type")) {
			range_type = jsonObject.get("range_type").toString();
		}

		if (jsonObject.containsKey("folder_type")) {
			folder_type = jsonObject.get("folder_type").toString();
		}

		if (jsonObject.containsKey("name")) {
			name = jsonObject.get("name").toString();
		}

		if (jsonObject.containsKey("data_content")) {
			// TODO
			// not yet implemented
		}

		if (jsonObject.containsKey("description")) {
			description = jsonObject.get("description").toString();
		}

		if (jsonObject.containsKey("mkdate")) {
			mkdate = Long.parseLong(jsonObject.get("mkdate").toString());
		}

		if (jsonObject.containsKey("chdate")) {
			chdate = Long.parseLong(jsonObject.get("chdate").toString());
		}

		if (jsonObject.containsKey("subfolders")) {

			((Map<Integer, JSONObject>) jsonObject.get("subfolders")).forEach((index, folderJson) -> {
				Id subfolderId = new Id(folderJson.get("id").toString());

				subfolders.add(subfolderId);
			});
		}
		
		if (jsonObject.containsKey("file_refs")) {
			
			((Map<Integer, JSONObject>) jsonObject.get("file_refs")).forEach((index, fileRefJson) -> {
				Id fileRefId = new Id(fileRefJson.get("id").toString());
				
				file_refs.add(fileRefId);
			});
		}
		

		return new Folder(is_visible, is_readable, is_writable, id, user_id, parent_id, range_id, range_type,
				folder_type, name, data_content, description, mkdate, chdate, subfolders, file_refs);
	}

	public boolean isIs_visible() {
		return is_visible;
	}

	public boolean isIs_readable() {
		return is_readable;
	}

	public boolean isIs_writable() {
		return is_writable;
	}

	public Id getId() {
		return id;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public Id getRange_id() {
		return range_id;
	}

	public String getRange_type() {
		return range_type;
	}

	public String getFolder_type() {
		return folder_type;
	}

	public String getName() {
		return name;
	}

	public List<Id> getData_content() {
		return data_content;
	}

	public String getDescription() {
		return description;
	}

	public long getMkdate() {
		return mkdate;
	}

	public long getChdate() {
		return chdate;
	}

	public List<Id> getSubfolders() {
		return subfolders;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Folder)) {
			return false;
		}
		Folder other = (Folder) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
