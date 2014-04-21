package edu.njust.sem.wa;

import java.awt.Toolkit;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import edu.njust.sem.wa.dao.PagesDao;
import edu.njust.sem.wa.util.TimeUtil;
import edu.njust.sem.wa.util.WebDriverUtil;

public class SearchPages {
	private int taskId;
	private String baseUrl;
	private String currUrl;
	private WebDriver driver;
	private int totalPageNum;
	private int currPageNum = 1;

	private SearchPage currPage;
	private String keyword;
	private String endTime;// 本次搜索的结束时间（高级搜索）
	private String startTime;// 本次搜索的开始时间（高级搜索）
	private String deadline;// 本搜索所属任务的开始时间
	private PagesDao pagesDao = new PagesDao();
	private Task task;// 当前搜索页面所属的任务
	private static SearchPages searchPages;

	private SearchPages() {
	}

	public static void clear() {
		searchPages = null;
	}

	public static SearchPages getInstance() {
		if (searchPages == null) {
			searchPages = new SearchPages();
		}
		return searchPages;
	}

	private SearchPages(String keyword, String endTime, int currPageNum) {
		this.setKeyword(keyword);
		this.setEndTime(endTime);
		this.currPageNum = currPageNum;
	}

	/**
	 * 初始化搜索页面，成功则返回true,否则返回false
	 * 
	 * @return
	 */
	public boolean init() {
		this.driver = WebDriverUtil.getDefaultDriver();
		baseUrl = getBaseUrl(keyword);
		WebDriverUtil.get(this.getCurrUrl());
		Document doc = getCachedCurrPage().getDocument();
		checkBlack(doc);
		isNoResult(doc);
		totalPageNum = getTotalPageNum();
		if ( currPageNum == 1 && totalPageNum == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获取搜索所得页面总数，最大为50页
	 * 
	 * @return
	 */
	public int getTotalPageNum() {
		Document doc = getCachedCurrPage().getDocument();
		Element e = null;
		int i = 0;
		while ((e = doc.getElementsByClass("search_page_M").last()) == null) {
			TimeUtil.sleep(3000);
			doc = getCurrPage().getDocument();
			if (++i > 10) {
				return 0;
			}
		}
		String str = e.select("li:nth-last-child(2)").text();
		int max = Integer.parseInt(str);
		return max;
	}

	public SearchPage getCachedCurrPage() {
		if (currPage == null) {
			currPage = new SearchPage(driver);
		}
		return currPage;
	}

	public SearchPage getCurrPage() {
		currPage = new SearchPage(driver);
		return currPage;
	}

	public SearchPage nextPage() {
		currPageNum++;
		if (currPageNum > totalPageNum) {
			saveCurrTime();
			pagesDao.updateCurrPageNum(this);
			searchPages = null;
			return null;
		}
		WebDriverUtil.get(this.getCurrUrl());
		TimeUtil.sleep(10000);
		currPage = new SearchPage(driver);
		Document doc = currPage.getDocument();
		if (checkBlack(doc) || isNoResult(doc)) {
			currPageNum--;
			nextPage();
		}
		updateTotalPageNum();
		// 更新数据库中的curr_page字段
		pagesDao.updateCurrPageNum(this);
		return currPage;
	}

	/**
	 * 第一页显示的总页数并不一定准确，所以每次翻页都要更新本次搜索结果的总页数。
	 */
	private void updateTotalPageNum() {
		if (currPageNum <= 3) {
			int max = getTotalPageNum();
			if (max > 0 && max < totalPageNum) {
				totalPageNum = max;
				pagesDao.updateTotalPageNum(this);
			}
		}
	}

	private boolean checkBlack(Document doc) {
		Elements elesVcode = doc.select("p.code_tit");
		if (elesVcode != null && elesVcode.size() > 0) {
			String text = elesVcode.first().text();
			if (text.equals("你的行为有些异常，请输入验证码：")) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null, "已输入验证码。。。",
						"被拉黑了亲，你懂的。。。", JOptionPane.ERROR_MESSAGE, null);
				return true;
			}
		}
		return false;
	}

	private boolean isNoResult(Document doc) {
		Elements elesNoResult = doc.select("p.noresult_tit");
		if (elesNoResult != null && elesNoResult.size() > 0) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "出问题了,没有检索结果", "刷新一下",
					JOptionPane.ERROR_MESSAGE, null);
			return true;
		}
		return false;
	}

	/**
	 * 把本次搜索的最后一条微博的发布时间记录到搜索记录里面，以方便设定下次搜索的截止时间
	 */
	private void saveCurrTime() {
		Document doc = currPage.getDocument();
		Element weiboE = doc.select(".feed_list").last();
		Element dateE = weiboE.select("p.info>a").first();
		String datetime = dateE.attr("title");
		this.setStartTime(datetime);
		pagesDao.updateStartTime(this);
		task.setCurrTime(this.getStartTime());
		task.updateTaskCurrTime();
	}

	/**
	 * 
	 * @param keyword
	 */
	public String getBaseUrl(String keyword) {
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String perfix = "http://s.weibo.com/wb/";
		String suffix = "&xsort=time&scope=ori&timescope=custom:"
				+ this.getDeadline() + ":" + this.getEndTime() + "&nodup=1";
		return perfix + keyword + suffix;
	}

	public String getCurrUrl() {
		currUrl = baseUrl + "&page=" + currPageNum;
		return currUrl;
	}

	public void setCurrUrl(String currUrl) {
		this.currUrl = currUrl;
	}

	public int getCurrPageNum() {
		return currPageNum;
	}

	public void setCurrPageNum(int currPageNum) {
		this.currPageNum = currPageNum;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setStartTime(String startTime) {
		startTime = startTime.split(":")[0];
		startTime = startTime.replace(" ", "-");
		this.startTime = startTime;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.taskId = task.getTaskId();
		this.deadline = task.getStartTime();
		this.task = task;
	}

	public String getStartTime() {
		return startTime;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

}
