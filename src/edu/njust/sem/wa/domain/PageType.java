package edu.njust.sem.wa.domain;

public enum PageType {
	Comment("http://weibo.com/aj/comment/big?_wv=5&"), Forward(
			"http://weibo.com/aj/mblog/info/big?_wv=5&");
	private String baseUrl;

	private PageType(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}
}
