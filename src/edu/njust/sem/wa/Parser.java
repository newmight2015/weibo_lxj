package edu.njust.sem.wa;

import edu.njust.sem.wa.dao.DBUtil;
import edu.njust.sem.wa.dao.PagesDao;
import edu.njust.sem.wa.service.BloggerService;
import edu.njust.sem.wa.service.HotWeiboService;
import edu.njust.sem.wa.service.WeiboService;
import edu.njust.sem.wa.util.FetchUtil;
import edu.njust.sem.wa.util.Logger;

public class Parser {
	private WeiboService weiboService = WeiboService.getInstance();
	private PagesDao pagesDao = new PagesDao();
	private HotWeiboService hotWeiboService = HotWeiboService.getInstance();

	public Parser() {
	}

	public void run() {
		try {
			//hotWeiboService.fetchCommentsAndForwards();
			finishUncomplatedSearchPages();
			Task task = null;
			while ((task = Task.getTask()) != null) {
				SearchPages pages = null;
				while((pages = task.getNextSearchPages()) != null){
					crawlSearchPages(pages);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FetchUtil.close();
			DBUtil.closeConnection();
			System.out.println("done!");
		}
	}

	/**
	 * 若还有搜索页面没有完成抓取，则继续抓取
	 */
	private void finishUncomplatedSearchPages() {
		SearchPages pages = null;
		while ((pages = pagesDao.getUncomplatedSearchPages()) != null) {
			crawlSearchPages(pages);
		}
		hotWeiboService.fetchCommentsAndForwards();
	}

	/**
	 * 抓取页面
	 * 
	 * @param pages
	 */
	private void crawlSearchPages(SearchPages pages) {
		do {
			String html = null;
			do {
				html = pages.getCurrPage().getHtml();
				Logger.log("当前页面url：" + pages.getCurrUrl());
				BloggerService.drawAllBloggerFromPage(html);
			} while (!weiboService.parseWeibo(html));
		} while (pages.nextPage() != null);
		SearchPages.clear();
	}

}
