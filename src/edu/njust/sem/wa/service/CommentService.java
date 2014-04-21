package edu.njust.sem.wa.service;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.wa.dao.CommentDao;
import edu.njust.sem.wa.domain.Comment;
import edu.njust.sem.wa.domain.PageType;
import edu.njust.sem.wa.util.FetchUtil;
import edu.njust.sem.wa.util.JsonUtils;
import edu.njust.sem.wa.util.Logger;
import edu.njust.sem.wa.util.ParseUtil;

public class CommentService {

	private static CommentService commentService;
	private CommentDao commentDao = CommentDao.getInstance();

	private CommentService() {

	}

	public static CommentService getInstance() {
		if (commentService == null) {
			commentService = new CommentService();
		}
		return commentService;
	}

	public void insertComments(List<Comment> comments) {
		for (Comment comment : comments) {
			commentDao.insertComment(comment);
		}
	}

	public void extartExchangeInfo() {
		try {
			commentDao.extartExchangeInfo();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 当一条微博的评论条数大于10，跳转到该微博的主页，抓取所有的评论信息
	 * 
	 * @param commentE
	 * @return
	 */
	public List<Comment> getCommentsFromPage(String html) {
		Document doc = Jsoup.parse(html);
		Elements commentsE = doc.getElementsByClass("comment_list");
		if (commentsE == null || commentsE.size() == 0) {
			return null;
		}
		List<Comment> comments = new LinkedList<>();
		for (Element commentE : commentsE) {
			Comment comment = new Comment();
			Element infoE = commentE.select(".info>a").first();
			String data = infoE.attr("action-data");
			String info = infoE.text().trim();
			if (info.length() > 0) {
				info = info.substring(1, info.length() - 1);
				int approveNum = Integer.parseInt(info);
				comment.setApproveNum(approveNum);
			}
			int x1 = data.indexOf("=") + 1;
			int y1 = data.indexOf("&");
			int x2 = data.lastIndexOf("=") + 1;
			comment.setCommentId(data.substring(x1, y1));
			comment.setWeiboId(data.substring(x2));
			
			//
			String commentHtml = commentE.html();
			commentHtml = commentHtml.substring(0,commentHtml.indexOf("<div class=\"info\">"));
			Document commentDoc = Jsoup.parse(commentHtml);
			Element userE = commentDoc.select("dd>a").first();
			String usercard = userE.attr("usercard");
			String idstr = usercard.substring(3);
			comment.setReviewerId(idstr);

			Element contentE = commentDoc.getElementsByTag("dd").first();
			ParseUtil.replaceImgWithAlt(contentE);

			String text = commentDoc.text();
			int index = text.lastIndexOf('(');
			String content = null;
			if (index > -1) {
				content = text.substring(0, index);
			}
			comment.setContent(content);
			int end = text.lastIndexOf(')');
			String date = text.substring(index + 1, end);
			comment.setCommentTime(date);
			comments.add(comment);
		}
		Logger.log("已抓取" + comments.size() + "条评论");
		return comments;
	}

	/**
	 * 当一条微博评论条数不大于10时，直接从搜索页面获取评论
	 * 
	 * @param commentE
	 * @param weiboId
	 * @return
	 */
	public List<Comment> parseComments(String html, String weiboId) {
		Document doc = Jsoup.parse(html);
		Elements commentEs = doc.getElementsByClass("comment_list");
		List<Comment> comments = new LinkedList<>();
		if (commentEs == null || commentEs.size() == 0) {
			return null;
		}
		for (Element commentE : commentEs) {
			Comment comment = new Comment();
			comment.setWeiboId(weiboId);
			comment.setCommentId(commentE.attr("comment_id"));

			Element userE = commentE.select("dd>a[usercard]").first();
			String usercard = userE.attr("usercard");
			String idstr = usercard.substring(3);
			comment.setReviewerId(idstr);

			String text = commentE.text();
			int index = text.lastIndexOf('(');
			String content = null;
			if (index > -1) {
				content = text.substring(0, index);
			}
			comment.setContent(content);
			int end = text.lastIndexOf(')');
			String date = text.substring(index + 1, end);
			comment.setCommentTime(date);

			Element infoE = commentE.getElementsByClass("info").first();
			String info = infoE.text();
			int approveNum = ParseUtil.getNum(info);
			if (approveNum >= 1) {
				comment.setApproveNum(approveNum);
			}
			comments.add(comment);
		}
		if (comments.size() == 0) {
			System.out.println("抓取小量评论失败……");
			return null;
		}
		Logger.log("已抓取" + comments.size() + "条评论");
		return comments;
	}

	/**
	 * 调用ajax接口获取不多于10条的评论信息
	 * 
	 * @param mid
	 * @return
	 */
	public String requestComments(String mid) {
		String url = PageType.Comment.getBaseUrl() + "&id=" + mid;
		String json = FetchUtil.requestJson(url);
		String html = JsonUtils.getSinaHtml(json);
		return html;
	}
}
