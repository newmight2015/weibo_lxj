package edu.njust.sem.wa.domain;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.wa.SearchPages;
import edu.njust.sem.wa.util.ParseUtil;

public class Weibo {
	private String weiboId;
	private String mid;
	private String bloggerId;
	private String bloggerName;
	private int forwardNum;
	private int commentNum;
	private int approveNum;
	private int collectNum;
	private String searchKeyword;
	private String url;
	private String publishTime;
	private String content;
	private String origin;

	public Weibo() {

	}

	/**
	 * @param weiboE
	 */
	public Weibo(Element weiboE) {
		this.setMid(weiboE.attr("mid"));
		this.setSearchKeyword(SearchPages.getInstance().getKeyword());
		ParseUtil.replaceImgWithAlt(weiboE);
		Element userE = weiboE.select("dd.content>p>a").first();
		String bloggerName = userE.attr("nick-name");
		this.setBloggerName(bloggerName);
		String bloggerId = userE.attr("usercard");
		bloggerId = bloggerId.substring(bloggerId.indexOf('=')+1,bloggerId.indexOf('&'));
		this.setBloggerId(bloggerId);
		String content = weiboE.select("dd.content>p").first().text();
		int index1 = content.indexOf('：');
		content = content.substring(index1+1);
		this.setContent(content);
		Element dateE = weiboE.select("p.info>a").first();
		Element originE = weiboE.select("p.info>a").last();
		String url = dateE.attr("href");
		this.setUrl(url);
		// 微博id的编码规则为 发布者id_微博url最后一个反斜杠后的字符
		this.setWeiboId(bloggerId + "_"
				+ url.substring(url.lastIndexOf("/") + 1));
		String date = dateE.attr("title");
		this.setPublishTime(date);
		String origin = originE.text();
		this.setOrigin(origin);

		Elements infoes = weiboE.select("p.info>span>a");
		for (int i = 0; i < infoes.size(); i++) {
			String text = infoes.get(i).text();
			int index = text.indexOf("(");
			if (index > -1) {
				int times = Integer.parseInt(text.substring(index + 1,
						text.indexOf(")")));
				switch (i) {
				case 0:
					this.setApproveNum(times);
					break;
				case 1:
					this.setForwardNum(times);
					break;
				case 2:
					this.setCollectNum(times);
					break;
				case 3:
					this.setCommentNum(times);
				}
			}
		}
	}

	public String getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}

	public String getBloggerId() {
		return bloggerId;
	}

	public void setBloggerId(String bloggerId) {
		this.bloggerId = bloggerId;
	}

	public String getBloggerName() {
		return bloggerName;
	}

	public void setBloggerName(String bloggerName) {
		this.bloggerName = bloggerName;
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

	public int getApproveNum() {
		return approveNum;
	}

	public void setApproveNum(int approveNum) {
		this.approveNum = approveNum;
	}

	public int getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(int collectNum) {
		this.collectNum = collectNum;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	@Override
	public String toString() {
		return "Weibo [weiboId=" + weiboId + ", mid=" + mid + ", bloggerId="
				+ bloggerId + ", bloggerName=" + bloggerName + ", forwardNum="
				+ forwardNum + ", commentNum=" + commentNum + ", approveNum="
				+ approveNum + ", collectNum=" + collectNum
				+ ", searchKeyword=" + searchKeyword + ", url=" + url
				+ ", publishTime=" + publishTime + ", content=" + content
				+ ", origin=" + origin + "]";
	}

}
