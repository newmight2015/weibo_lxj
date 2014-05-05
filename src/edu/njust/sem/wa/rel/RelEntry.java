package edu.njust.sem.wa.rel;

public class RelEntry {
	private String originBloggerId;
	private String info;
	private String forwardBloggerId;

	public RelEntry() {
	}

	public RelEntry(String origin, String blogger) {
		this.originBloggerId = origin.toLowerCase();
		this.setForwardBloggerId(blogger.toLowerCase());
	}

	public String getOriginBloggerId() {
		return originBloggerId;
	}

	public void setOriginBloggerId(String originBloggerId) {
		this.originBloggerId = originBloggerId;
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "RelEntry [content=" + originBloggerId + ", blogger="
				+ forwardBloggerId + "]";
	}

	public String getForwardBloggerId() {
		return forwardBloggerId;
	}

	public void setForwardBloggerId(String forwardBloggerId) {
		this.forwardBloggerId = forwardBloggerId;
	}
}
