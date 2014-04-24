package edu.njust.sem.wa.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;

public class FetchUtil {
	private static CloseableHttpClient httpClient = null;
	private static final int TIME_OUT = 10;
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0";
	private static String html;
	private static RequestConfig config = RequestConfig.custom()
			.setSocketTimeout(TIME_OUT * 1000)
			.setConnectTimeout(TIME_OUT * 1000)
			.setConnectionRequestTimeout(TIME_OUT * 1000).build();

	public static String getCachedHtml() {
		WebDriver driver = WebDriverUtil.getDefaultDriver();
		if (html == null) {
			html = driver.getPageSource();
		}
		return html;
	}

	public static String getHtml() {
		WebDriver driver = WebDriverUtil.getDefaultDriver();
		return driver.getPageSource();
	}

	public static CloseableHttpClient getRegisteredClient() {
		if (httpClient == null) {
			WebDriver driver = WebDriverUtil.getDefaultDriver();
			String url = driver.getCurrentUrl();
			if (!url.startsWith("http://weibo.com/")
					&& !url.startsWith("http://s.weibo.com/")) {
				driver.get("http://weibo.com/");
				WebDriverUtil.waitPageLoad(driver);
			}
			Set<org.openqa.selenium.Cookie> cs = driver.manage().getCookies();
			CookieStore cookieStore = new BasicCookieStore();
			for (org.openqa.selenium.Cookie c : cs) {
				BasicClientCookie2 cookie = new BasicClientCookie2(c.getName(),
						c.getValue());
				cookie.setDomain(c.getDomain());
				cookie.setExpiryDate(c.getExpiry());
				cookie.setPath(c.getPath());
				cookieStore.addCookie(cookie);
			}
			HttpClientBuilder builder = HttpClientBuilder.create();
			builder.setDefaultCookieStore(cookieStore);
			builder.setUserAgent(USER_AGENT);
			Header headerReferer = new BasicHeader("Referer",
					"http://weibo.com/");
			Header headerEncoding = new BasicHeader("Accept-Encoding",
					"gzip, deflate");
			List<Header> headers = new ArrayList<Header>();
			headers.add(headerReferer);
			headers.add(headerEncoding);
			builder.setDefaultHeaders(headers);
			builder.setDefaultRequestConfig(config);
			httpClient = builder.build();
		}
		return httpClient;
	}

	public static String requestBlogger(String url) {
		if (httpClient == null) {
			httpClient = getRegisteredClient();
		}
		CloseableHttpResponse res = null;
		try {
			HttpGet get = new HttpGet(url);
			get.setConfig(config);
			res = (CloseableHttpResponse) httpClient.execute(get);
			if (res.getStatusLine().getStatusCode() == 200) {
				String str = EntityUtils.toString(res.getEntity(), "UTF-8");
				EntityUtils.consumeQuietly(res.getEntity());
				return str;
			} else {
				Logger.log("可能被拉黑了，请仔细检查,状态码："
						+ res.getStatusLine().getStatusCode());
				TimeUtil.sleep(60 * 1000);
				return requestBlogger(url);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			TimeUtil.sleep(60 * 1000);
			return requestBlogger(url);
		} finally {
			try {
				if (res != null) {
					res.close();
					res = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String requestJson(String url) {
		if (httpClient == null) {
			httpClient = getRegisteredClient();
		}
		HttpGet get = new HttpGet(url);
		get.setHeader("X-Requested-With", "XMLHttpRequest");
		get.setConfig(config);
		CloseableHttpResponse res = null;
		try {
			res = httpClient.execute(get);
			if (res.getStatusLine().getStatusCode() == 200) {
				String jsonstr = EntityUtils.toString(res.getEntity(), "UTF-8");
				EntityUtils.consumeQuietly(res.getEntity());
				return jsonstr;
			} else {
				Logger.log("可能被拉黑了，请仔细检查,状态码："
						+ res.getStatusLine().getStatusCode());
				// JOptionPane.showMessageDialog(null, "ajax请求失败，休息一下");
				TimeUtil.sleep(2 * 60 * 1000);
				return requestJson(url);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				res.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void close() {
		if (httpClient != null) {
			try {
				httpClient.close();
				httpClient = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
