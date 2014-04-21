package edu.njust.sem.wa.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverUtil {
	private static WebDriver driver = null;
	/**
	 * 
	 */
	public static WebDriver getLoginedDriver() {
		System.setProperty("webdriver.firefox.bin", "D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
		ProfilesIni allProfiles = new ProfilesIni();
		FirefoxProfile profile = allProfiles.getProfile("default");
		profile.setPreference("permissions.default.image", 2);
		WebDriver driver = new FirefoxDriver(profile);

		driver.get("http://weibo.com/");
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().startsWith("����΢��");
			}
		});
		WebElement elementName = driver.findElement(By.name("username"));
		elementName.sendKeys("lixiaojun629@163.com");
		WebElement elementPassword = driver.findElement(By.name("password"));
		elementPassword.sendKeys("9fa3194a");
		WebElement button = driver.findElement(By.className("W_btn_g"));
		button.click();
		try {
			Thread.sleep(5000);
			System.out.println(driver.getPageSource());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return driver;
	}

	/**
	 * 
	 * @return
	 */
	public static WebDriver getDefaultDriver() {
		if(driver == null){
			ProfilesIni allProfiles = new ProfilesIni();
			FirefoxProfile profile = allProfiles.getProfile("default");
			driver =  new FirefoxDriver(profile);
		}
		return driver;
	}

	public static void waitLoaded(WebDriver driver, final String title) {
		(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().startsWith(title);
			}
		});
	}
	public static void get(String url){
		driver.get(url);
		waitPageLoad(driver);
	}
	public static void waitPageLoad(final WebDriver driver) {
		(new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		});
	}

	public static void waitUserPanePop(final WebDriver driver,
			final String username) {
		(new WebDriverWait(driver, 3)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				WebElement userNameE = null;
				try {
					userNameE = driver.findElement(By
							.xpath("//div[@class='bg']//a[@title='" + username
									+ "']"));
				} catch (RuntimeException e) {
				}
				if (userNameE != null) {
					System.out.println(userNameE.getText() + " success");
					return true;
				}
				return false;
			}
		});
	}
}
