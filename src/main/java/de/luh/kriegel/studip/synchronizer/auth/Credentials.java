package de.luh.kriegel.studip.synchronizer.auth;

import org.json.simple.JSONObject;

/**
 * Model class containing username and password as well as parsing into json
 * @author Josef
 *
 */
public class Credentials {

	private String username;
	private String password;

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("username", username);
		jsonObject.put("password", password);
		
		return jsonObject;
	}

	@Override
	public String toString() {
//		return username + " : " + password.replaceAll(".", "*");
		return toJson().toJSONString();
	}
	
}
