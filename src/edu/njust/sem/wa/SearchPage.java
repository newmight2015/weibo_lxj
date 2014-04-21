package edu.njust.sem.wa;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.njust.sem.wa.util.ParseUtil;

public class SearchPage {
	private String html;

	public SearchPage(WebDriver driver) {
		// showComment(driver);
		html = driver.getPageSource();
	}

	/**
	 * 点击页面的评论链接，展开评论面板
	 */
	public void showComment(WebDriver driver) {
		List<WebElement> comments = driver.findElements(By
				.xpath("//a[@action-type='feed_list_comment']"));

		for (WebElement e : comments) {
			String text = e.getText().trim();
			int amount = ParseUtil.getNum(text);
			if (amount >= 1 && amount <= 10) {
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].click();", e);
			}
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public Document getDocument() {
		return Jsoup.parse(html);
	}

	public String getHtml() {
		return html;
	}
}
