package edu.njust.sem.wa.domain;

public class Forward {
	private String forwardId;
	private String url;
	private String forwarderId;
	private String weiboId;
	private String content;
	private String publishTime;
	private int forwardNum;
	private int approveNum;

	public String getForwardId() {
		return forwardId;
	}

	public void setForwardId(String forwardId) {
		this.forwardId = forwardId;
	}

	public String getForwarderId() {
		return forwarderId;
	}

	public void setForwarderId(String forwarderId) {
		this.forwarderId = forwarderId;
	}

	public String getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public int getForwardNum() {
		return forwardNum;
	}

	public void setForwardNum(int forwardNum) {
		this.forwardNum = forwardNum;
	}

	public int getApproveNum() {
		return approveNum;
	}

	public void setApproveNum(int approveNum) {
		this.approveNum = approveNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Forward [forwardId=" + forwardId + ", url=" + url
				+ ", forwarderId=" + forwarderId + ", weiboId=" + weiboId
				+ ", content=" + content + ", publishTime=" + publishTime
				+ ", forwardNum=" + forwardNum + ", approveNum=" + approveNum
				+ "]";
	}
	
}
