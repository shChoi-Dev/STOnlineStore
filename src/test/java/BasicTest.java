import org.junit.Test;
import org.openqa.selenium.*;

import java.util.List;

import static junit.framework.TestCase.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Selenium & Junit4 test for BasicTest class.
 *
 * @author shChoi
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicTest extends TestHelper {

    private String username = "testAdmin";
    private String password = "testAdmin";

    @Test
    public void test01_titleExists(){
        String expectedTitle = "ST Online Store";
        String actualTitle = driver.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    /*  [관리자 계정]  */
    // 1. 계정 등록
    @Test
    public void test02_CreateAdmin() throws InterruptedException {
        createAdmin("testA1","testA123");
        Thread.sleep(1000);
        WebElement e = driver.findElement(By.id("notice"));
        String notice = e.getText();
        String expected = "User testA1 was successfully created.";
        Thread.sleep(1000);
        assertEquals(expected, notice);
        Thread.sleep(1000);
        deleteAdmin("testA1");
        Thread.sleep(1000);
    }
    // 1. 계정 등록(Negative)
    @Test
    public void test03_CreateAdmin_wrongConfirm(){
        createAdminWithConfirm("testA2","testCc213","badpw321");
        WebElement e = driver.findElement(By.cssSelector("#error_explanation > ul > li"));
        String notice = e.getText();
        String expected = "Password confirmation doesn't match Password";
        assertEquals(expected, notice);
    }

    // 2. 시스템 로그인
    @Test
    public void test04_Login(){
        login(username, password);
        WebElement e = driver.findElement(By.id("Products"));
        String product = e.getText();
        String expected = "Products";
        assertEquals(expected, product);
    }
    // 2. 시스템 로그인(Negative)
    @Test
    public void test05_LoginFalsePW() {
        login("Edward", "Lee");
        WebElement e = driver.findElement(By.id("notice"));
        String notice = e.getText();
        String expected = "Invalid user/password combination";
        assertEquals(expected, notice);
    }

    // 3. 시스템 로그아웃
    @Test
    public void test06_Logout() {
        login(username, password);
        logout();

        List<WebElement> eList = driver.findElements(By.cssSelector("#menu > ul > li"));
        String login = eList.get(1).getText();
        String expected = "Login";
        assertEquals(expected, login);
    }

    // 4. 계정 삭제
    @Test
    public void test07_DeleteAdmin() throws InterruptedException {
        createAdmin("test4del","test4321");
        Thread.sleep(1000);
        deleteAdmin("test4del");
        Thread.sleep(1000);
        WebElement e = driver.findElement(By.id("notice"));
        Thread.sleep(1000);
        waitForElementById("notice");
        String notice = e.getText();
        String expected = "User was successfully deleted.";
        Thread.sleep(1000);
        assertEquals(expected, notice);
    }
    // 4. 계정 삭제(Error)
    @Test
    public void test08_Delete_Last() throws InterruptedException {
        login(username, password);
        Thread.sleep(1000);
        deleteLastAdmin(username);
        Thread.sleep(1000);
        List<WebElement> eList = driver.findElements(By.cssSelector("#menu > ul > li"));
        String register = eList.get(2).getText();
        Thread.sleep(1000);
        String expected = "Register";
        assertEquals(expected, register);
    }

    // 5. 제품 추가
    @Test
    public void test09_AddProduct() throws InterruptedException {
        login(username, password);
        Thread.sleep(1000);
        String title = "Test Production",
                description = "This is test product",
                prodType = "Books",
                price = "10.99";
        Thread.sleep(1000);
        addProduct(title, description, prodType, price);

        assertNotNull(driver.findElement(By.id(title)));
        Thread.sleep(1000);
        deleteProduct(title);
    }
    // 5. 제품 추가(Negative)
    @Test
    public void test10_AddProduct_wrongPrice() throws InterruptedException {
        login(username, password);
        Thread.sleep(1000);
        String title = "2ND Test Product",
                description = "2ND Test Product",
                prodType = "Sunglasses",
                price = "price";
        Thread.sleep(1000);
        addProduct(title, description, prodType, price);

        WebElement e = driver.findElement(By.cssSelector("#error_explanation > ul > li"));
        String errorMsg = e.getText();
        String expected = "Price is not a number";
        Thread.sleep(1000);
        assertEquals(expected, errorMsg);
    }

    // 6. 제품 편집(Error)
    @Test
    public void test11_EditProduct_prodType() throws InterruptedException {
        login(username, password);
        String title = "Test Production Edit",
                description = "Test For Edit Product",
                prodType = "Books",
                price = "10.99";
        Thread.sleep(1000);
        addProduct(title, description, prodType, price);

        String newProdType = "Other";
        editProduct_prodType(title, newProdType);

        String actual = driver.findElement(By.xpath("//*[@id=\"main\"]/div/p[4]")).getText();
        driver.findElement(By.xpath("//*[@id=\"main\"]/div/div/a[2]"));
        driver.findElements(By.cssSelector("#menu > ul > li")).get(1).click();
        deleteProduct(title);
        assertEquals("Type: "+newProdType, actual);
    }

    // 7. 제품 삭제
    @Test
    public void test12_deleteProduct() throws InterruptedException {
        login(username, password);
        Thread.sleep(1000);
        String title = "Test Production",
                description = "Test For Delete Product",
                prodType = "Books",
                price = "99.99";
        Thread.sleep(1000);
        addProduct(title, description, prodType, price);
        Thread.sleep(1000);
        deleteProduct(title);
        Thread.sleep(1000);

        WebElement e = driver.findElement(By.id("notice"));
        waitForElementById("notice");
        String notice = e.getText();
        Thread.sleep(1000);
        String expected = "Product was successfully destroyed.";
        Thread.sleep(1000);
        assertEquals(expected,notice);
    }


    /*  [사용자 테스트]  */
    // 1. 카트 제품 추가
    @Test
    public void test13_AddCart() throws InterruptedException {
        addCart();
        Thread.sleep(1000);
        List<WebElement> eList = driver.findElements(By.cssSelector("#cart > table > tbody > tr"));
        assertEquals(3, eList.size() -1);
        assertEquals("Web Application Testing Book",driver.findElement(By.xpath("//*[@id=\"current_item\"]/td[2]")).getText());
    }

    // 2. 카트 제품 수량 증가 / 감소
    @Test
    public void test14_PlusMinusProduct() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.cssSelector("#B45593\\ Sunglasses_entry > div.price_line > form")).click();
        Thread.sleep(1000);
        plusProduct_currentItem(2);
        assertEquals("3×",driver.findElement(By.cssSelector("#current_item > td:nth-child(1)")).getText());
        minusProduct_currentItem(1);
        assertEquals("2×", driver.findElement(By.cssSelector("#current_item > td:nth-child(1)")).getText());
    }

    // 2. 카트 제품 수량 증가 / 감소(Error)
    @Test
    public void test15_PlusMinusProduct() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.cssSelector("#B45593\\ Sunglasses_entry > div.price_line > form")).click();
        Thread.sleep(1000);
        plusProduct_currentItem(2);
        assertEquals("€26.00",driver.findElement(By.cssSelector("#current_item > td:nth-child(3)")).getText());
    }

    // 3. 카트에서 항목 하나씩 삭제
    @Test
    public void test16_DeleteOneByOne() throws InterruptedException{
        addCart();
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("#delete_button > a")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("#delete_button > a")).click();
        Thread.sleep(1000);

        assertEquals(1, driver.findElements(By.className("cart_row")).size());
    }

    // 4. 카트 물품 전체 삭제
    @Test
    public void test17_EmptyCart() throws InterruptedException {
        addCart();
        Thread.sleep(1000);
        emptyCart();
        Thread.sleep(1000);
        WebElement e = driver.findElement(By.id("notice"));
        String notice = e.getText();
        String expected = "Cart successfully deleted.";
        Thread.sleep(1000);
        assertEquals(expected, notice);
    }

    // 5.1. 홈페이지에서 이름별로 제품 검색
    @Test
    public void test18_Search(){
        driver.get(baseUrl);
        driver.findElement(By.id("search_input")).sendKeys("Sunglass");

        assertEquals(driver.findElement(By.id("Sunglasses 2AR_entry")).getAttribute("style"),"");
        assertEquals(driver.findElement(By.id("Sunglasses B45593_entry")).getAttribute("style"),"");
        assertEquals(driver.findElement(By.id("B45593 Sunglasses_entry")).getAttribute("style"),"");
        assertEquals(driver.findElement(By.id("Web Application Testing Book_entry")).getAttribute("style"),"display: none;");
        driver.findElement(By.id("search_input")).clear();
    }
    // 5.2. 홈페이지에서 범주별로 필터링(Error)
    @Test
    public void test19_Other() throws InterruptedException{
        driver.get(baseUrl);
        driver.findElement(By.linkText("Other")).click();
        List<WebElement> eList = driver.findElements(By.className("entry"));
        Thread.sleep(1000);
        assertEquals(0, eList.size());
    }

    // 6. 물품 결제
    @Test
    public void test20_Purchase() throws InterruptedException{
        addCart();
        Thread.sleep(1000);
        String name="TestPurchaseName1",
                address="Test Purchase Address",
                email="Test@email.com",
                payType="Check";
        purchase(name, address, email, payType);
        String expected = "Thank you for your order";
        assertEquals(expected,driver.findElement(By.xpath("//*[@id=\"order_receipt\"]/h3")).getText());
    }
    // 6. 물품 결제(Negative)
    @Test
    public void test21_Purchase_wrongEmail() throws InterruptedException{
        addCart();
        Thread.sleep(1000);
        String name="TestPurchaseName2",
                address="Test Purchase Address",
                email="65521",
                payType="Credit card";
        purchase(name, address, email, payType);

        assertEquals("Email: "+email,driver.findElement(By.xpath("//*[@id=\"order_receipt\"]/p[3]")).getText());
    }
    // 6. 물품 결제(Negative)
    @Test
    public void test22_Purchase_noPayType() throws InterruptedException{
        addCart();
        Thread.sleep(1000);
        String name="TestPurchaseName3",
                address="Test Purchase Address",
                email="test@web.com",
                payType="Select a payment method";
        purchase(name, address, email, payType);

        assertEquals("Pay type is not included in the list",driver.findElement(By.cssSelector("#error_explanation > ul > li")).getText());
    }
    // 6. 물품 결제(Negative)
    @Test
    public void test23_Purchase_empty() throws InterruptedException {
        addCart();
        Thread.sleep(1000);
        String name = "",
                address = "",
                email = "";
        purchase_withoutPayType(name, address, email);
        assertEquals(4,driver.findElements(By.cssSelector("#error_explanation > ul > li")).size());
    }
}