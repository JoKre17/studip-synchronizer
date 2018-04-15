package de.luh.kriegel.studip.synchronizer.config;

public enum Endpoints {

	MESSAGES("/messages/write"),
	USER("/user");
	
	
	private final String path;
	
	private Endpoints(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
	
	@Override
	public String toString() {
		return path;
	}
	
}
