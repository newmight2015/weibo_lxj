package edu.njust.sem.wa.leader;

import edu.njust.sem.wa.dao.LeaderDao;
import edu.njust.sem.wa.domain.Blogger;

public class Leader {
	private String bloggerName;

	// 粉丝数量
	private int fansNum;
	// 发布微博数
	private int weiboNum;
	// 关注的人数
	private int followNum;
	// 在某个主题下，某微博用户在原创微博中，转发中，及评论中被@的次数加和
	private int atNum;
	// 在某个主题下，某微博用户的微博(包括原创和转发)被转发的数量
	private int forwardNum;
	// 在某个主题下，某微博用户的微博(包括原创和转发)被评论的数量
	private int commentNum;
	// 在某个主题下，某微博用户的微博参与评论的次数
	private int replyNum;
	// 微博用户是否为专家（根据微博用户的一句话简介来判断）
	private boolean isExpert = false;
	// 是否已认证
	private boolean hasVerified;
	// 媒体接触度
	private int mediaIndex;
	// 意见领袖指数（各个指标的加权和）
	private double leaderIndex;
	private Blogger blogger;
	private LeaderDao leaderDao = LeaderDao.getInstance();
	private String[] professions = { "媒体人", "记者", "医生", "护士", "公务员", "律师",
			"顾问", "教师", "教授", "总监" };

	public Leader() {

	}

	public Leader(Blogger blogger) {
		this.blogger = blogger;
		setBloggerName(blogger.getNikeName());
		fansNum = blogger.getFansNum();
		followNum = blogger.getFollowNum();
		weiboNum = blogger.getWeiboNum();
		if (blogger.getVerify() > 0) {
			hasVerified = true;
		} else {
			hasVerified = false;
		}

		String introduce = blogger.getIntroduce();
		setExpert(introduce);
	}

	public void init() {
		forwardNum = leaderDao.getForwardNumByName(blogger.getNikeName());
		commentNum = leaderDao.getCommentNumByName(blogger.getNikeName());
		replyNum = leaderDao.getReplayNumByName(blogger.getNikeName());
		mediaIndex = leaderDao.gerVerifyCommentNumByName(blogger.getNikeName());
		mediaIndex += leaderDao
				.getVerifyForwardNumByName(blogger.getNikeName());
		leaderIndex = 0.1208 * fansNum + 0.0915 * atNum + 0.2519 * forwardNum
				+ 0.2009 * commentNum + 0.0707 * weiboNum + 0.0122 * followNum
				+ 0.0209 * replyNum + 0.0378 * mediaIndex;
		if (hasVerified) {
			leaderIndex += 1.245;
		}
		if (isExpert) {
			leaderIndex += 0.378;
		}
	}

	public int getFansNum() {
		return fansNum;
	}

	public int getFollowNum() {
		return followNum;
	}

	public int getWeiboNum() {
		return weiboNum;
	}

	public int getAtNum() {
		return atNum;
	}

	public void addAtNum(int count) {
		this.atNum += count;
	}

	public int getForwardNum() {
		return forwardNum;
	}

	public void setForwardNum(int forwardNum) {
		this.forwardNum = forwardNum;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}

	public boolean isExpert() {
		return isExpert;
	}

	public void setExpert(String introduce) {
		if (introduce == null || introduce.length() == 0) {
			return;
		}
		for (String str : professions) {
			if (introduce.contains(str)) {
				isExpert = true;
				return;
			}
		}
	}

	public int getMediaIndex() {
		return mediaIndex;
	}

	public void setMediaIndex(int mediaIndex) {
		this.mediaIndex = mediaIndex;
	}

	public double getLeaderIndex() {
		return leaderIndex;
	}

	public void setLeaderIndex(double leaderIndex) {
		this.leaderIndex = leaderIndex;
	}

	public Blogger getBlogger() {
		return blogger;
	}

	public void setBlogger(Blogger blogger) {
		this.blogger = blogger;
	}

	public boolean isHasVerified() {
		return hasVerified;
	}

	public void setHasVerified(boolean hasVerified) {
		this.hasVerified = hasVerified;
	}

	@Override
	public String toString() {
		return "Leader [fansNum=" + fansNum + ", followNum=" + followNum
				+ ", atNum=" + atNum + ", forwardNum=" + forwardNum
				+ ", commentNum=" + commentNum + ", replyNum=" + replyNum
				+ ", isExpert=" + isExpert + ", hasVerified=" + hasVerified
				+ ", mediaIndex=" + mediaIndex + ", leaderIndex=" + leaderIndex
				+ "]";
	}

	public String getBloggerName() {
		return bloggerName;
	}

	public void setBloggerName(String bloggerName) {
		this.bloggerName = bloggerName;
	}

}
