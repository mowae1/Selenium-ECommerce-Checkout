package org.example;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    WebDriver driver;
    WebDriverWait wait;
    // Set up the Chrome WebDriver and open the SauceDemo website

    @BeforeTest
    public void SetUp() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\pc\\Downloads\\chromedriver-win32\\chromedriver-win32\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
    }
    // This test logs in using a randomly selected username from a predefined list

    @Test
    public void LoginIn() throws InterruptedException {
        List<String> usernames = List.of(
                "standard_user",
                "problem_user",
                "performance_glitch_user",
                "error_user",
                "visual_user"
        );

        Random random = new Random();
        String usernameSelected = usernames.get(random.nextInt(usernames.size()));

        driver.findElement(By.id("user-name")).sendKeys(usernameSelected);
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        Thread.sleep(2000);
        driver.findElement(By.name("login-button")).click();
        Thread.sleep(2000);
    }
    // This test extracts the names and prices of products and saves them to a CSV file

    @Test
    public void productsPrices(){
        List<String[]> productData = new ArrayList<>();

        try {
            List<WebElement> items = driver.findElements(By.className("inventory_item_description"));

            for (WebElement item : items) {
                String productName = item.findElement(By.className("inventory_item_name")).getText();
                String productPrice = item.findElement(By.className("inventory_item_price")).getText();
                productData.add(new String[]{productName, productPrice});
            }

            writeToCSV(productData, "items.csv");
            System.out.println("Data extraction and CSV writing completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // This method writes the extracted product data into a CSV file

    private static void writeToCSV(List<String[]> data, String fileName) {
        try (FileWriter writer = new FileWriter(fileName);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Product Name", "Price"))) {
            for (String[] product : data) {
                csvPrinter.printRecord(product[0], product[1]);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // This test adds three items to the shopping cart

    @Test
    public void addToCart(){
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))).click();
        System.out.println("First item added to cart");

        wait.until(ExpectedConditions.elementToBeClickable(By.name("add-to-cart-sauce-labs-bolt-t-shirt"))).click();
        System.out.println("Second item added to cart");

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Test.allTheThings() T-Shirt (Red)"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.name("add-to-cart"))).click();
        System.out.println("Third item added to cart");

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"shopping_cart_container\"]/a"))).click();
    }
    // This test proceeds with the checkout process by entering user details and finalizing the order

    @Test
    public void checkOut(){
        wait.until(ExpectedConditions.elementToBeClickable(By.name("checkout"))).click();
        driver.findElement(By.name("firstName")).sendKeys("Mohab");
        driver.findElement(By.id("last-name")).sendKeys("Wael");
        driver.findElement(By.xpath("//*[@id=\"postal-code\"]")).sendKeys("11728");
        driver.findElement(By.name("continue")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"finish\"]"))).click();
    }
    // This test verifies whether the checkout process was completed successfully

    @Test
    public void checkOutComplete(){
        WebElement done = driver.findElement(By.xpath("//*[@id=\"header_container\"]/div[2]"));
        String complete = done.getText();
        if("Checkout: Complete!".equals(complete)){
            driver.findElement(By.name("back-to-products")).click();
            System.out.println("Checkout Complete");
        }
        else {
            System.out.println("Error in Checkout, Please try again");
        }
    }
    // This test closes the browser after all tests are completed

    @AfterTest
    public void ending(){
        driver.quit();
    }
}
