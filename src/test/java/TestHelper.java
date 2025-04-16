import org.junit.After;
import org.junit.Before;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Selenium test for TestHelper class.
 *
 * @author shChoi
 */

public class TestHelper {

    static WebDriver driver;
	
	// 관리자 웹사이트 링크
    String baseUrlAdmin = "http://127.0.0.1:3000/admin";
	
	// 웹사이트 링크
    String baseUrl = "http://127.0.0.1:3000";

    @Before
    public void setUp(){
        /**
         *   Chrome 웹 브라우저 사용시
         *   System.setProperty("webdriver.chrome.driver", "C:\\Users\\...\\chromedriver.exe");
         *   driver = new ChromeDriver();
         *   Firefox 웹 브라우저 사용시
         *   System.setProperty("webdriver.gecko.driver", "C:\\Users\\...\\geckodriver.exe");
         *   driver = new FirefoxDriver();
         *   */

        // Edge 웹 브라우저 사용시
        System.setProperty("webdriver.edge.driver", "C:\\Users\\...\\msedgedriver.exe");
        driver = new EdgeDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(baseUrl);

    }

    void waitForElementById(String id){
        new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    /*  [관리자 계정]  */

    // 1. 계정 등록
    void createAdmin(String name, String password){
        driver.get(baseUrlAdmin);
        driver.findElement(By.linkText("Register")).click();

        driver.findElement(By.id("user_name")).sendKeys(name);
        driver.findElement(By.id("user_password")).sendKeys(password);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(password);

        By loginButton = By.xpath("//input[@value='Create User']");
        driver.findElement(loginButton).click();
    }
    // 1. 계정 등록(Negative)
    void createAdminWithConfirm(String name, String password, String confirm){
        driver.get(baseUrlAdmin);
        driver.findElement(By.linkText("Register")).click();

        driver.findElement(By.id("user_name")).sendKeys(name);
        driver.findElement(By.id("user_password")).sendKeys(password);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(confirm);

        By loginButton = By.xpath("//input[@value='Create User']");
        driver.findElement(loginButton).click();
    }


    // 2. 시스템 로그인
    void login(String username, String password){
        driver.get(baseUrlAdmin);

        driver.findElement(By.linkText("Login")).click();

        driver.findElement(By.id("name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);

        By loginButtonXpath = By.xpath("//input[@value='Login']");
        driver.findElement(loginButtonXpath).click();
    }

    // 3. 시스템 로그아웃
    void logout(){
        WebElement logout = driver.findElement(By.linkText("Logout"));
        logout.click();

        waitForElementById("Admin");
    }

    // 4. 계정 삭제
    void deleteAdmin(String name){
        driver.get(baseUrlAdmin);
        driver.findElement(By.cssSelector("#"+name+" > a:nth-child(2)")).click();
    }
    // 4. 계정 삭제(Negative)
    void deleteLastAdmin(String name){
        driver.get(baseUrlAdmin);
        driver.findElement(By.cssSelector("#"+name+" > a:nth-child(2)")).click();
        driver.findElement(By.xpath("//*[@class='users_column']//table//tbody//tr//td[4]//a[1]")).click();
        driver.switchTo().alert().accept();
    }

    // 5. 제품 추가
    void addProduct(String title, String description, String prodType, String price){
        driver.findElement(By.xpath("//*[@id=\"new_product_div\"]/a")).click();
        driver.findElement(By.id("product_title")).sendKeys(title);
        driver.findElement(By.id("product_description")).sendKeys(description);
        Select select = new Select(driver.findElement(By.id("product_prod_type")));
        select.selectByVisibleText(prodType);
        driver.findElement(By.id("product_price")).sendKeys(price);
        driver.findElement(By.xpath("//*[@id=\"new_product\"]/div[5]/input")).click();
    }

    // 6. 제품 편집
    void editProduct_prodType(String title, String newProdType){
        driver.findElement(By.xpath("//*[@id=\""+title+"\"]/td[3]/a")).click();
        Select select = new Select(driver.findElement(By.id("product_prod_type")));
        select.selectByVisibleText(newProdType);
        driver.findElement(By.name("commit")).click();
    }

    // 7. 제품 삭제
    void deleteProduct(String title){
        driver.findElement(By.xpath("//*[@class='products_column']//table//tbody//tr[@id=\""+title+"\"]//td[4]//a[1]")).click();
    }

    /*  [사용자 테스트]  */
    // 1. 카트 제품 추가
    void addCart() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.cssSelector("#B45593\\ Sunglasses_entry > div.price_line > form")).click();
        driver.findElement(By.cssSelector("#Sunglasses\\ 2AR_entry > div.price_line > form")).click();
        driver.findElement(By.xpath("//*[@id='Web Application Testing Book_entry']//h3[1]//a[1]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@value='Add to Cart']")).click();
    }

    /**
     *   2. 카트 제품 수량 증가 / 감소
     *   3. 카트 항목 하나씩 삭제
     */

    void plusProduct_currentItem(int item) throws InterruptedException {
        for(int i=0; i<item; i++){
            driver.findElement(By.cssSelector("#current_item > td:nth-child(5) > a")).click();
            Thread.sleep(1000);
        }
    }
    void minusProduct_currentItem(int item) throws InterruptedException {
        for(int i=0; i<item; i++){
            driver.findElement(By.cssSelector("#current_item > td:nth-child(4) > a")).click();
            Thread.sleep(1000);
        }
    }

    // 4. 카트 물품 전체 삭제
    void emptyCart(){
        driver.findElement(By.xpath("//*[@id=\"cart\"]/form[1]/input[2]")).click();
    }

    // 6. 물품 결제
    void purchase(String name, String address, String email, String payType){
        driver.findElement(By.cssSelector("#checkout_button > input[type=submit]")).click();
        driver.findElement(By.id("order_name")).sendKeys(name);
        driver.findElement(By.id("order_address")).sendKeys(address);
        driver.findElement(By.id("order_email")).sendKeys(email);
        Select select = new Select(driver.findElement(By.id("order_pay_type")));
        select.selectByVisibleText(payType);
        driver.findElement(By.name("commit")).click();
    }

    // 6. 물품 결제(Negative)
    void purchase_withoutPayType(String name, String address, String email){
        driver.findElement(By.cssSelector("#checkout_button > input[type=submit]")).click();
        driver.findElement(By.id("order_name")).sendKeys(name);
        driver.findElement(By.id("order_address")).sendKeys(address);
        driver.findElement(By.id("order_email")).sendKeys(email);
        driver.findElement(By.name("commit")).click();
    }


    @After
    public void tearDown(){
        driver.close();
    }

}