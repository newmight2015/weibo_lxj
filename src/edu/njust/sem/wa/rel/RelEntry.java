package edu.njust.sem.wa.rel;

public class RelEntry {
	private String origin;
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	private String blogger;

	public RelEntry(String origin, String blogger) {
		this.origin = origin.toLowerCase();
		this.blogger = blogger.toLowerCase();
	}

	@Override
	public String toString() {
		return "RelEntry [content=" + origin + ", blogger=" + blogger + "]";
	}

	
	public String getBlogger() {
		return blogger;
	}

	public void setBlogger(String blogger) {
		this.blogger = blogger;
	}

}
