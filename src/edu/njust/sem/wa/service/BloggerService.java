package edu.njust.sem.wa.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import com.alibaba.fastjson.JSON;

import edu.njust.sem.wa.dao.BloggerDao;
import edu.njust.sem.wa.domain.Blogger;
import edu.njust.sem.wa.domain.BloggerJson;
import edu.njust.sem.wa.util.FetchUtil;
import edu.njust.sem.wa.util.Logger;
import edu.njust.sem.wa.util.WebDriverUtil;

public class BloggerService {
	private static Map<String, Blogger> unfullUsers = new HashMap<>();
	private static BloggerDao bloggerDao = new BloggerDao();

	public static void drawAllBloggerFromPage(String html) {
		System.out.println("getting all user.....");
		Document doc = Jsoup.parse(html);
		Elements bloggerEs = doc.select("a[usercard]");
		for (Element bloggerE : bloggerEs) {
			Blogger blogger = null;
			try {
				String nikeName = bloggerE.text().trim();
				if (nikeName.contains("@")) {
					nikeName = nikeName.split("@")[1];
				}
				if (bloggerDao.hasBlogger(nikeName)) {
					//System.out.println("用户[" + nikeName + "]已抓取");
					continue;
				}
				blogger = new Blogger();
				blogger.setNikeName(nikeName);
				blogger = getBlogger(blogger);
				if (blogger != null && blogger.getId() != null) {
					bloggerDao.insertBlogger(blogger);
				}
				else{
					//System.out.println(blogger);
				}
			} catch (Exception e) {
				e.printStackTrace();
				blogger.setIntroduce("");//博主简介中往往包含特殊字符导致插入失败
				bloggerDao.insertBlogger(blogger);
				continue;
			}
		}
		Logger.log("已抓取页面中的"+bloggerEs.size()+"个用户");
	}

	/**
	 * 
	 * @param userWe
	 * @return
	 * @throws IOException
	 */
	public static Blogger getBlogger(Blogger blogger) {

		BloggerJson bloggerJson = null;
		try {
			if(blogger.getId() == null){
				bloggerJson = getBloggerPopHtmlByName(blogger.getNikeName());
			}else{
				bloggerJson = getBloggerPopHtmlById(blogger.getId()+"");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bloggerJson != null) {
			int code = bloggerJson.getCode();
			if (code == 100000) {
				String html = bloggerJson.getData();
				fillBlogger(html, blogger);
				return blogger;
			} else if(code == 100001){
				System.out.println("用户[" + blogger.getNikeName() + "]不存在");
			}else{
				Logger.log("在抓取博主信息时，发生了一些诡异的事情");
			}
		}
		return null;
	}

	/**
	 * 
	 * @param html
	 * @param blogger
	 * @return
	 */
	public static Blogger fillBlogger(String html, Blogger blogger) {
		Document doc = Jsoup.parse(html);
		Element genderE = doc.select(".name em").first();
		String gender = genderE.attr("title");
		Element bloggerE = genderE.nextElementSibling();
		String url = bloggerE.attr("href");
		int index = url.indexOf('?');
		if (index > -1) {
			url = url.substring(0, index);
		}
		String idstr = bloggerE.attr("uid");
		blogger.setGender(gender);
		blogger.setUrl(url);
		blogger.setId(idstr);
		Element e = doc.select("dl.name").first();
		String text = e.text();
		text = text.replaceAll("万", "5000");
		text = text.substring(text.indexOf(" "));
		String[] arr = text.split("\\W+");
		blogger.setFollowNum(Integer.parseInt(arr[1]));
		blogger.setFansNum(Integer.parseInt(arr[2]));
		blogger.setWeiboNum(Integer.parseInt(arr[3]));

		Elements eles = doc.getElementsByClass("info");
		if (eles != null && eles.size() > 0) {
			String introduce = eles.first().text();
			blogger.setIntroduce(introduce);
		}

		Elements userInfoEs = doc.getElementsByClass("user_info");
		if (userInfoEs != null && userInfoEs.size() > 0) {
			Element userInfoE = userInfoEs.first();
			eles = userInfoE
					.select("a[href^=http://s.weibo.com/user?type=tag]");
			if (eles != null && eles.size() > 0) {
				String address = eles.first().attr("title");
				blogger.setAddress(address);
			}
			eles = userInfoE
					.select("a[href^=http://s.weibo.com/user?type=school]");
			if (eles != null && eles.size() > 0) {
				String school = eles.first().attr("title");
				blogger.setSchool(school);
			}
			eles = userInfoE
					.select("a[href^=http://s.weibo.com/user?type=work]");
			if (eles != null && eles.size() > 0) {
				String work = eles.first().attr("title");
				blogger.setCompany(work);
			}
		}

		eles = doc.getElementsByClass("approve_co");
		if (eles != null && eles.size() > 0) {
			blogger.setVerify(2);
		}
		eles = doc.getElementsByClass("approve");
		if (eles != null && eles.size() > 0) {
			blogger.setVerify(1);
		}
		eles = doc.getElementsByAttributeValue("title", "微博达人");
		if (eles != null && eles.size() > 0) {
			blogger.setDaren(true);
		}
		eles = doc.getElementsByAttributeValue("title", "微博会员");
		if (eles != null && eles.size() > 0) {
			blogger.setMember(true);
		}

		return blogger;
	}

	/**
	 * 获取鼠标悬停在博主名上时，弹出层的html文本
	 * 
	 * @param nikeName
	 * @return
	 * @throws IOException
	 */
	public static BloggerJson getBloggerPopHtmlByName(String nikeName)
			throws IOException {
		nikeName = URLEncoder.encode(nikeName, "utf-8");
		String url = "http://weibo.com/aj/user/newcard?type=1&mark=&name="
				+ nikeName + "&_t=1";
		String str = FetchUtil.requestBlogger(url);
		int end = str.indexOf("\"}") + 2;
		str = str.substring(5, end);
		return JSON.parseObject(str, BloggerJson.class);
	}
	/**
	 * 获取鼠标悬停在博主名上时，弹出层的html文本
	 * 
	 * @param nikeName
	 * @return
	 * @throws IOException
	 */
	public static BloggerJson getBloggerPopHtmlById(String id)
			throws IOException {
		String url = "http://weibo.com/aj/user/newcard?type=1&mark=&id="+id+"&_t=1";
		String str = FetchUtil.requestBlogger(url);
		int end = str.indexOf("\"}") + 1;
		str = str.substring(5, end+1);
		return JSON.parseObject(str, BloggerJson.class);
	}

	public static void getFullBloggersFromHomePage(WebDriver driver) {
		for (String username : unfullUsers.keySet()) {
			try {
				Blogger user = unfullUsers.get(username);
				driver.get(user.getUrl());
				WebDriverUtil.waitPageLoad(driver);
				String html = driver.getPageSource();
				Document doc = Jsoup.parse(html);
				Element genderE = doc.select("div.tags a:first-child em")
						.first();
				String gender = null;
				if (genderE != null) {
					gender = genderE.attr("title");
				}
				String text = doc.select("ul.user_atten").text();
				String[] arr = text.split("\\W+");
				user.setGender(gender);
				user.setFollowNum(Integer.parseInt(arr[0]));
				user.setFansNum(Integer.parseInt(arr[1]));
				user.setWeiboNum(Integer.parseInt(arr[2]));
				// bloggers.put(user.getNikeName(), user);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

		}
	}
}
