package com.ngalam.app;

import java.io.File;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MainApp {

	private WebDriver driver;

	@BeforeClass
	public void classSetup() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("chromedriver.exe").getFile());
		String absolutePath = file.getAbsolutePath();
		System.setProperty("webdriver.chrome.driver", absolutePath);
	}

	@BeforeMethod
	public void setup() {
		driver = new ChromeDriver();
	}

	@AfterMethod
	public void cleanUp() {
		driver.close();
		driver.quit();
	}

	@Test
	public void testAmazonSearch() throws Exception {
		int expectedCartCount = 3;
		driver.get("http://www.amazon.com");
		for (int i = 0; i < expectedCartCount; i++) {
			searchAndAddToCart();
		}
		int actualCartCount = Integer.parseInt(findWebElementPresence(By.id("nav-cart-count")).getText());
		Assert.assertEquals(expectedCartCount, actualCartCount, "Items count mismatch");

	}

	private void searchAndAddToCart() throws Exception {
		findWebElementVisibility(By.id("twotabsearchtextbox")).sendKeys("iphone x");
		findWebElementPresence(By.className("nav-input")).click();
		findWebElementPresence(By.name("s-ref-checkbox-17352550011")).click();
		findWebElementVisibility(By.cssSelector(
				"#result_0 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a > h2"))
						.click();
		findWebElementPresence(By.id("add-to-cart-button")).click();
	}

	private WebElement findWebElementPresence(By locator) throws Exception {
		waitForJSandJQueryToLoad();
		WebElement webElement = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			webElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		} catch (Exception e) {
			System.out.println("Unable to Find Web Element By presence" + e);
			throw e;
		}
		return webElement;
	}

	private WebElement findWebElementVisibility(By locator) throws Exception {
		waitForJSandJQueryToLoad();
		WebElement webElement = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch (Exception e) {
			System.out.println("Unable to Find Web Element By Visibility" + e);
			throw e;
		}
		return webElement;
	}

	public boolean waitForJSandJQueryToLoad() throws InterruptedException {

		WebDriverWait wait = new WebDriverWait(driver, 30);

		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
				} catch (Exception e) {
					// no jQuery present
					return true;
				}
			}
		};

		// wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		return wait.until(jQueryLoad) && wait.until(jsLoad);
	}

}
