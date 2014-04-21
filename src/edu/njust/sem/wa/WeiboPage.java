package edu.njust.sem.wa;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.wa.domain.HotWeibo;
import edu.njust.sem.wa.domain.PageType;
import edu.njust.sem.wa.service.HotWeiboService;
import edu.njust.sem.wa.util.FetchUtil;
import edu.njust.sem.wa.util.JsonUtils;
import edu.njust.sem.wa.util.Logger;
import edu.njust.sem.wa.util.TimeUtil;

/**
 * 微博主页
 * 
 * @author lxj
 * 
 */
public class WeiboPage {
	private String html;
	private PageType type;
	private HotWeibo hotWeibo;
	private int currPageNum = 1;
	private int totalPageNum = 1;
	private String baseUrl;
	private String actionData;
	private HotWeiboService hotWeiboService = HotWeiboService.getInstance();

	public WeiboPage(HotWeibo hotWeibo, PageType type) {
		this.hotWeibo = hotWeibo;
		this.type = type;
		init();

	}

	/**
	 * 初始化总页数totalPageNum和用于翻页的action-data
	 */
	private void init() {
		baseUrl = type.getBaseUrl() + "id=" + hotWeibo.getWeiboId();
		int requestTimes = 0;
		if(type.equals(PageType.Forward)){
			requestTimes = hotWeibo.getForwardRequestTimes();
			if(requestTimes == 0){
				hotWeibo.setForwardRequestTimes(1);
			}
		}else{
			requestTimes = hotWeibo.getCommentRequestTimes();
			if(requestTimes == 0){
				hotWeibo.setCommentRequestTimes(1);
			}
		}
		if (requestTimes == 0) {
			// 获取总页数及action-data
			getMaxPageNumAndActionData();
			hotWeiboService.updateHotWeibo(hotWeibo);
		} else {
			String actionData = null;
			if (this.type.equals(PageType.Forward)) {
				actionData = hotWeibo.getForwardActionData();
				if (actionData != null && actionData.length() > 0) {
					baseUrl = type.getBaseUrl() + actionData;
					currPageNum = hotWeibo.getCurrForwardPageNum();
					totalPageNum = hotWeibo.getTotalForwardPageNum();
				}
			} else {
				actionData = hotWeibo.getCommentActionData();
				if (actionData != null && actionData.length() > 0) {
					baseUrl = type.getBaseUrl() + actionData;
					currPageNum = hotWeibo.getCurrCommentPageNum();
					totalPageNum = hotWeibo.getTotalCommentPageNum();
				}
			}
		}
	}

	private void getMaxPageNumAndActionData() {
		html = this.getCurrPageHtml();
		Document doc = Jsoup.parse(html);
		Elements pagesEs = doc.getElementsByClass("W_pages_minibtn");
		Elements nextPageEs = null;
		if (pagesEs != null && pagesEs.size() > 0) {
			nextPageEs = pagesEs.first().getElementsMatchingOwnText("下一页");
			Element maxPageNumE = pagesEs.first()
					.select("a:nth-last-child(2)").first();
			String text = maxPageNumE.text();
			totalPageNum = Integer.parseInt(text);
		}
		if (nextPageEs != null && nextPageEs.size() > 0) {
			String param = nextPageEs.first().attr("action-data");
			if (param != null && param.trim().length() > 0) {
				param = param.substring(0, param.lastIndexOf('&'));
				this.setActionData(param);
				baseUrl =type.getBaseUrl()+ this.getActionData();
				if (this.type.equals(PageType.Forward)) {
					hotWeibo.setForwardActionData(param);
					hotWeibo.setTotalForwardPageNum(totalPageNum);
					hotWeibo.setCurrForwardPageNum(1);
				} else {
					hotWeibo.setCommentActionData(param);
					hotWeibo.setTotalCommentPageNum(totalPageNum);
					hotWeibo.setCurrCommentPageNum(1);
				}
			}
		}
	}

	/**
	 * 翻页
	 * 
	 * @return 若有下一页则翻页并返回true，若没有下一页则直接返回false
	 */
	public boolean next() {
		TimeUtil.sleep(1000);
		++currPageNum;
		if (currPageNum <= totalPageNum) {
			if (type.equals(PageType.Forward)) {
				hotWeibo.setCurrForwardPageNum(currPageNum);
			} else {
				hotWeibo.setCurrCommentPageNum(currPageNum);
			}
			hotWeiboService.updateHotWeibo(hotWeibo);
			html = null;
			return true;
		} else {
			html = null;
			if (hotWeibo.getCurrCommentPageNum() > hotWeibo
					.getTotalCommentPageNum()
					&& hotWeibo.getCurrForwardPageNum() == hotWeibo
							.getTotalForwardPageNum()) {
				hotWeiboService.deleteHotWeibo(hotWeibo);
			}
			return false;
		}
	}

	public String getCurrPageHtml() {
		if(html == null){
			Logger.log("请求url:" + getCurrUrl() + " totalPageNum:" + totalPageNum);
			String json = FetchUtil.requestJson(getCurrUrl());
			html = JsonUtils.getSinaHtml(json);
		}
		if(html == null){
			System.out.println("exception");
		}
		return html;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getCurrUrl() {
		return baseUrl + "&page=" + currPageNum;
	}

	public String getActionData() {
		return actionData;
	}

	public void setActionData(String actionData) {
		this.actionData = actionData;
	}
}
