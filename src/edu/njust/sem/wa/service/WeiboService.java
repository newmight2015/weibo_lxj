package edu.njust.sem.wa.service;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.wa.dao.WeiboDao;
import edu.njust.sem.wa.domain.Comment;
import edu.njust.sem.wa.domain.Forward;
import edu.njust.sem.wa.domain.HotWeibo;
import edu.njust.sem.wa.domain.Weibo;
import edu.njust.sem.wa.util.Logger;
import edu.njust.sem.wa.util.TimeUtil;

public class WeiboService {
	private WeiboDao weiboDao = WeiboDao.getInstance();
	private CommentService commentService = CommentService.getInstance();
	private ForwardService forwardService = ForwardService.getInstance();
	private HotWeiboService hotWeiboService = HotWeiboService.getInstance();
	private static WeiboService weiboService;

	private WeiboService() {
	};

	public static WeiboService getInstance() {
		if (weiboService == null) {
			weiboService = new WeiboService();
		}
		return weiboService;
	}

	public boolean parseWeibo(String html) {
		Document doc = Jsoup.parse(html);
		Elements weibos = doc.select(".feed_list");
		if (weibos == null || weibos.size() == 0) {
			TimeUtil.sleep(50000);
			return false;
		}
		for (Element weiboE : weibos) {
			try {
				Weibo weibo = new Weibo(weiboE);
				weiboService.insertWeibo(weibo);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		Logger.log("已抓取" + weibos.size() + "条微博");
		return true;
	}

	public void insertWeibo(Weibo weibo) {
		int commentNum = weibo.getCommentNum();
		int forwardNum = weibo.getForwardNum();
		HotWeibo hotWeibo = new HotWeibo();
		if (commentNum > 0) {
			if (commentNum > 10) {
				hotWeibo.setUrl(weibo.getUrl());
				hotWeibo.setHasComment(false);
			} else {
				fetchComments(weibo);
				hotWeibo.setHasComment(true);
			}
		}
		if (forwardNum > 0) {
			if (forwardNum > 10) {
				hotWeibo.setUrl(weibo.getUrl());
				hotWeibo.setHasForward(false);
			} else {
				fetchForwards(weibo);
				hotWeibo.setHasForward(true);
			}
		}
		boolean flag = weiboDao.insertWeibo(weibo);
		// 如果微博插入数据库成功，并且该微博有待抓取的评论和转发信息，则把该微博作为热门微博保存到数据库中
		if (flag && hotWeibo.getUrl() != null) {
			hotWeibo.setWeiboId(weibo.getMid());
			insertHotWeibo(hotWeibo);
			hotWeiboService.fetchCommentsAndForwards();
		}
	}

	private void insertHotWeibo(HotWeibo hotWeibo) {
		hotWeiboService.insertHotWeibo(hotWeibo);
	}

	private void fetchForwards(Weibo weibo) {
		String weiboMid = weibo.getMid();
		String html = forwardService.requestForwards(weiboMid);
		List<Forward> forwards = forwardService.parseForwards(html, weiboMid);
		if (forwards != null && forwards.size() > 0) {
			forwardService.insertForwards(forwards);
		} else {
			Logger.log("请求 " + weibo.getForwardNum() + " 条转发失败,微博URL："
					+ weibo.getUrl());
		}
	}

	private void fetchComments(Weibo weibo) {
		String html = commentService.requestComments(weibo.getMid());
		List<Comment> comments = commentService.getCommentsFromPage(html);
		if (comments != null && comments.size() > 0) {
			commentService.insertComments(comments);
		} else {
			Logger.log("请求" + weibo.getCommentNum() + "条评论失败，微博URL："
					+ weibo.getUrl());
		}
	}
}
