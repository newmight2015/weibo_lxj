package edu.njust.sem.wa.test;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.njust.sem.wa.domain.Blogger;
import edu.njust.sem.wa.domain.BloggerJson;
import edu.njust.sem.wa.domain.Forward;
import edu.njust.sem.wa.service.BloggerService;
import edu.njust.sem.wa.util.FetchUtil;
import edu.njust.sem.wa.util.JsonUtils;
import edu.njust.sem.wa.util.ParseUtil;
import edu.njust.sem.wa.util.TimeUtil;

public class Test {
	public static void main(String[] args) {
		// test();
		// next();
		test3();
	}
	public static void test3(){
		String content = "//@Sun卡农:好人 //@笔得潘: //@N先森说攒压力不如攒钱:作为南京的医务人员，" +
				"对于此事决不能容忍，不管打人者是谁，#新浪新闻让红包飞# 昨天日下午，【一路走好…】官再大，" +
				"也大不过法律。谢谢，@温医师，作为护士谢谢你！ http://t.cn/zTU2iys 说了我们的心声（分享自 @凤凰网）";
		content = "法制日报？看来打人者背景很深！/江苏女护士被打追踪：视频监控证实检察官未动手 - http://t.cn/8F3BuHE（分享自 @凤凰网）";
		content = content.replace('：', ':');
		// 去掉转发，只保留原创内容
		int x1 = content.lastIndexOf("//@");
		if (x1 != -1) {
			content = content.substring(x1);
			content = content.substring(content.indexOf(":")+1);
		}
		content = content.replaceAll("#.*?#", "");
		content = content.replaceFirst("【.*?】", "");
		content = content.replaceAll("@.+?\\b", "");
		content = content.replaceAll("[a-zA-z]+://[^\\s]*", "");
		System.out.println(content);
	}
	public static void test2() {
		String[] names = { "Lxt-Nancy", "德亨技术", "程序员的那些事", "Ada李力", "InfoQ",
				"左洪斌" };
		for (String name : names) {
			try {
				BloggerJson b = BloggerService.getBloggerPopHtmlByName(name);
				//parseBlogger(b.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static void test1() {
		String[] arr = { "5秒前", "5分钟前", "今天 10:38", "3月23日 03:51" };
		for (String str : arr) {
			String time = TimeUtil.formmatDate(str);
			System.out.println(time);
		}
	}

	public static void test() {
		String url = "http://weibo.com/aj/mblog/repost/small?_wv=5&mid=3689841214191430&d_expanded=off&expanded_status=1&_t=0&__rnd=1395229776512";
		String json = FetchUtil.requestJson(url);
		String html = JsonUtils.getSinaHtml(json);
		Document doc = Jsoup.parse(html);
		Elements forwardEs = doc.select(".comment_list>dd:not(.clear)");
		for (Element forwardE : forwardEs) {
			Forward forward = new Forward();
			String id = forwardE.getElementsByTag("a").first().attr("href")
					.substring(1);
			forward.setForwarderId(id);
			Elements imgs = forwardE.getElementsByTag("img");
			// 把评论中出现的图片（表情等）替换为相应的文字
			for (Element e : imgs) {
				String alt = e.attr("alt");
				e.html(alt);
			}
			Element infoE = forwardE.getElementsByClass("info").first();
			Element approveE = infoE.getElementsByAttributeValue("title", "赞")
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
			System.out.println(forward);
		}
	}
}
