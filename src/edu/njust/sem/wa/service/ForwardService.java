package edu.njust.sem.wa.service;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.wa.dao.ForwardDao;
import edu.njust.sem.wa.domain.Forward;
import edu.njust.sem.wa.domain.HotWeibo;
import edu.njust.sem.wa.domain.PageType;
import edu.njust.sem.wa.util.FetchUtil;
import edu.njust.sem.wa.util.JsonUtils;
import edu.njust.sem.wa.util.Logger;
import edu.njust.sem.wa.util.ParseUtil;

public class ForwardService {
	private static ForwardService forwardService;
	private ForwardDao forwardDao;
	private HotWeiboService hotWeiboService;

	private ForwardService() {
		forwardDao = ForwardDao.getInstance();
	}

	public static ForwardService getInstance() {
		if (forwardService == null) {
			forwardService = new ForwardService();
		}
		return forwardService;
	}
	public void extartExchangeInfo(){
		try {
			forwardDao.extartExchangeInfo();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertForwards(List<Forward> forwards) {
		if (hotWeiboService == null) {
			hotWeiboService = HotWeiboService.getInstance();
		}
		for (Forward forward : forwards) {
			// 如果新转发的微博被转发的次数多于10次，则抓取该微博的评论
			if (forward.getForwardNum() >= 10) {
				HotWeibo hotWeibo = new HotWeibo();
				hotWeibo.setWeiboId(forward.getForwardId());
				hotWeibo.setUrl(forward.getUrl());
				hotWeibo.setHasComment(false);
				hotWeibo.setHasForward(true);
				hotWeiboService.insertHotWeibo(hotWeibo);
			}
			forwardDao.insertForward(forward);
		}
	}

	public String requestForwards(String weiboMid) {
		String url = PageType.Forward.getBaseUrl()+"&id="+weiboMid;
		String json = FetchUtil.requestJson(url);
		String html = JsonUtils.getSinaHtml(json);
		return html;
	}

	public List<Forward> parseForwards(String html, String weiboId) {
		Document doc = Jsoup.parse(html);
		Elements forwardEs = doc.select(".comment_list>dd:not(.clear)");
		if (forwardEs == null || forwardEs.size() == 0) {
			return null;
		}
		List<Forward> forwards = new LinkedList<>();
		for (Element forwardE : forwardEs) {
			Forward forward = new Forward();
			forward.setWeiboId(weiboId);
			String id = forwardE.getElementsByAttribute("usercard").first().attr("usercard");
			forward.setForwarderId(id.substring(3));
			ParseUtil.replaceImgWithAlt(forwardE);

			Element infoE = forwardE.getElementsByClass("info").first();
			Element approveE = infoE.getElementsByAttributeValueEnding("title", "赞")
					.first();
			int approveNum = ParseUtil.getNum(approveE.text());
			forward.setApproveNum(approveNum);
			String actionData = approveE.attr("action-data");
			String mid = actionData.split("&")[2].substring(4);
			forward.setForwardId(mid);
			Element contentE = forwardE.getElementsByTag("em").first();
			String content = contentE.text();
			forward.setContent(content);
			Element dateE = forwardE.getElementsByAttribute("date").first();
			String forwardUrl = "http://weibo.com" + dateE.attr("href");
			forward.setUrl(forwardUrl);
			forward.setPublishTime(dateE.attr("title"));
			Element forwardNumE = infoE.select("span.fr>a").last();
			int forwardNum = ParseUtil.getNum(forwardNumE.text());
			forward.setForwardNum(forwardNum);
			forwards.add(forward);
		}
		Logger.log("已抓取"+forwards.size()+"条转发");
		return forwards;
	}
}
