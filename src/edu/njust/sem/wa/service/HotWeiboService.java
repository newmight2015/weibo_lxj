package edu.njust.sem.wa.service;

import java.sql.SQLException;
import java.util.List;

import edu.njust.sem.wa.WeiboPage;
import edu.njust.sem.wa.dao.HotWeiboDao;
import edu.njust.sem.wa.domain.Comment;
import edu.njust.sem.wa.domain.Forward;
import edu.njust.sem.wa.domain.HotWeibo;
import edu.njust.sem.wa.domain.PageType;
import edu.njust.sem.wa.util.Logger;

public class HotWeiboService {
	private static HotWeiboService hotWeiboService;
	private HotWeiboDao hotWeiboDao;
	private CommentService commentService;
	private ForwardService forwardService;

	private HotWeiboService() {
		commentService = CommentService.getInstance();
		hotWeiboDao = HotWeiboDao.getInstance();
	}

	public static HotWeiboService getInstance() {
		if (hotWeiboService == null) {
			hotWeiboService = new HotWeiboService();
		}
		return hotWeiboService;
	}

	public void insertHotWeibo(HotWeibo hotWeibo) {
		hotWeiboDao.insertHotWeibo(hotWeibo);
	}

	public HotWeibo getOneHotWeibo() {
		try {
			return hotWeiboDao.getOneHotWeibo();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 跳转到微博主页抓取微博的转发和评论
	 */
	public void fetchCommentsAndForwards() {

		HotWeibo hotWeibo = null;
		while ((hotWeibo = hotWeiboService.getOneHotWeibo()) != null) {
			try {
				if (!hotWeibo.isHasComment()) {
					if (fetchComments(hotWeibo)) {
						hotWeibo.setHasComment(true);
					} else {
						hotWeibo.setCommentRequestTimes(hotWeibo
								.getCommentRequestTimes() + 1);
					}
				}
				if (!hotWeibo.isHasForward()) {
					if (fetchForwards(hotWeibo)) {
						hotWeibo.setHasForward(true);
					} else {
						hotWeibo.setForwardRequestTimes(hotWeibo
								.getForwardRequestTimes() + 1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				hotWeiboService.updateHotWeibo(hotWeibo);
			}
		}
	}

	public void updateHotWeibo(HotWeibo hotWeibo) {
		hotWeiboDao.updateHotWeibo(hotWeibo);
	}

	private boolean fetchForwards(HotWeibo hotWeibo) {
		if (forwardService == null) {
			forwardService = ForwardService.getInstance();
		}
		WeiboPage weiboPage = new WeiboPage(hotWeibo, PageType.Forward);
		String html = null;
		do {
			html = weiboPage.getCurrPageHtml();
			if (hotWeibo.getForwardRequestTimes() > 10) {
				Logger.log(hotWeibo+" 出现异常！请检查！");
				return true;
			}
			List<Forward> forwards = forwardService.parseForwards(html,
					hotWeibo.getWeiboId());
			if (forwards == null) {
				return false;
			}
			BloggerService.drawAllBloggerFromPage(html);
			forwardService.insertForwards(forwards);
		} while (weiboPage.next());
		return true;
	}

	private boolean fetchComments(HotWeibo hotWeibo) {
		String html = null;
		WeiboPage weiboPage = new WeiboPage(hotWeibo, PageType.Comment);
		do {
			html = weiboPage.getCurrPageHtml();
			if (hotWeibo.getCommentRequestTimes() > 10) {
				Logger.log(hotWeibo+" 出现异常！请检查！");
				return true;
			}
			List<Comment> comments = commentService.getCommentsFromPage(html);
			if (comments == null) {
				return false;
			}
			BloggerService.drawAllBloggerFromPage(html);
			commentService.insertComments(comments);
		} while (weiboPage.next());
		return true;
	}

	public void deleteHotWeibo(HotWeibo hotWeibo) {
		hotWeiboDao.deleteHotWeibo(hotWeibo);
	}
}
