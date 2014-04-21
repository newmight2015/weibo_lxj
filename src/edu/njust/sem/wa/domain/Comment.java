package edu.njust.sem.wa.domain;

import edu.njust.sem.wa.util.TimeUtil;

public class Comment {
	private String commentId;
	private String weiboId;
	private String content;
	private String commentTime;
	private String reviewerId;
	private int approveNum;;

	public Comment() {

	}

	@Override
	public String toString() {
		return "Comment [commentId=" + commentId + ", weiboId=" + weiboId
				+ ", content=" + content + ", commentTime=" + commentTime
				+ ", reviewerId=" + reviewerId + ", approveNum=" + approveNum
				+ "]";
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = TimeUtil.formmatDate(commentTime);
	}


	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public int getApproveNum() {
		return approveNum;
	}

	public void setApproveNum(int approveNum) {
		this.approveNum = approveNum;
	}
}
