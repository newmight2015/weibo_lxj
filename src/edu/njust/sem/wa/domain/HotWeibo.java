package edu.njust.sem.wa.domain;

/**
 * 热门微博，评论和转发数量较多的微博
 * 
 * @author lxj
 * 
 */
public class HotWeibo {
	private String weiboId;
	private String url;
	// 是否已抓取转发
	private boolean hasForward;
	// 是否已抓取评论
	private boolean hasComment;
	private String forwardActionData = "";

	private String commentActionData = "";
	private int forwardRequestTimes;
	private int commentRequestTimes;

	private int currForwardPageNum = 1;
	private int totalForwardPageNum = 1;
	private int currCommentPageNum = 1;
	private int totalCommentPageNum = 1;

	public int getCurrForwardPageNum() {
		return currForwardPageNum;
	}

	public void setCurrForwardPageNum(int currForwardPageNum) {
		this.currForwardPageNum = currForwardPageNum;
	}

	public int getTotalForwardPageNum() {
		return totalForwardPageNum;
	}

	public void setTotalForwardPageNum(int totalForwardPageNum) {
		this.totalForwardPageNum = totalForwardPageNum;
	}

	public int getForwardRequestTimes() {
		return forwardRequestTimes;
	}

	public void setForwardRequestTimes(int forwardRequestTimes) {
		this.forwardRequestTimes = forwardRequestTimes;
	}

	public int getCommentRequestTimes() {
		return commentRequestTimes;
	}

	public void setCommentRequestTimes(int commentRequestTimes) {
		this.commentRequestTimes = commentRequestTimes;
	}

	public int getCurrCommentPageNum() {
		return currCommentPageNum;
	}

	public void setCurrCommentPageNum(int currCommentPageNum) {
		this.currCommentPageNum = currCommentPageNum;
	}

	public int getTotalCommentPageNum() {
		return totalCommentPageNum;
	}

	public void setTotalCommentPageNum(int totalCommentPageNum) {
		this.totalCommentPageNum = totalCommentPageNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isHasForward() {
		return hasForward;
	}

	public void setHasForward(boolean hasForward) {
		this.hasForward = hasForward;
	}

	public boolean isHasComment() {
		return hasComment;
	}

	public void setHasComment(boolean hasComment) {
		this.hasComment = hasComment;
	}


	public String getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}

	public String getForwardActionData() {
		return forwardActionData;
	}

	public void setForwardActionData(String forwardActionData) {
		this.forwardActionData = forwardActionData;
	}

	public String getCommentActionData() {
		return commentActionData;
	}

	public void setCommentActionData(String commentActionData) {
		this.commentActionData = commentActionData;
	}

	@Override
	public String toString() {
		return "HotWeibo [weiboId=" + weiboId + ", url=" + url
				+ ", hasForward=" + hasForward + ", hasComment=" + hasComment
				+ ", forwardActionData=" + forwardActionData
				+ ", commentActionData=" + commentActionData
				+ ", forwardRequestTimes=" + forwardRequestTimes
				+ ", commentRequestTimes=" + commentRequestTimes
				+ ", currForwardPageNum=" + currForwardPageNum
				+ ", totalForwardPageNum=" + totalForwardPageNum
				+ ", currCommentPageNum=" + currCommentPageNum
				+ ", totalCommentPageNum=" + totalCommentPageNum + "]";
	}
}
