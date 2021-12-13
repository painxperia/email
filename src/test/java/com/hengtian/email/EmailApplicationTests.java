package com.hengtian.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class EmailApplicationTests {

    @Value("${email.name}")
    private String emailName;

    @Value("${email.password}")
    private String emailPassword;

    @Test
    void contextLoads() throws Exception {
        WebDriver driver = login();

        //休眠2s
        TimeUnit.SECONDS.sleep(2);

        WebElement search = driver.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[3]/div/div[1]/div[2]/div[4]/div/div/div[1]/div/div/button/span[2]"));
        search.click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        //查找将要删除的邮件发送人
        WebElement input = driver.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[3]/div/div[1]/div[2]/div[4]/div/div/div[1]/div/div/div/div[1]/form/div/input"));
        input.sendKeys("pbi01");
        input.sendKeys(Keys.ENTER);
        TimeUnit.SECONDS.sleep(3);
        WebElement nowEmail = driver.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[3]/div/div[1]/div[2]/div[1]/div[2]/div/div[2]/div/div[1]/div/div[2]"));
        nowEmail.click();

        //点击加载更多
        TimeUnit.SECONDS.sleep(2);
        boolean b = true;
        String title = "如果没有找到您要查找的内容，则使用更具体的搜索词重试一次。";
        while (b) {

            try {
                WebElement limitClick = driver.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[3]/div/div[1]/div[2]/div[4]/div/div/div[6]/div[2]/div[1]/div[1]/div/div/div[2]/span"));
                String text = limitClick.getText();
                if (StringUtils.equals(title, text)) {
                    b = false;
                } else {
                    WebElement moreData = driver.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[3]/div/div[1]/div[2]/div[4]/div/div/div[6]/div[2]/div[1]/div[1]/div/div/div[2]/button/span[1]"));
                    moreData.click();
                }
            } catch (Exception e) {
            }
        }
        TimeUnit.SECONDS.sleep(3);
        WebElement divData = driver.findElement(By.xpath("/html/body/div[2]/div/div[3]/div[3]/div/div[1]/div[2]/div[4]/div/div/div[6]/div[2]/div[1]/div[1]/div/div/div[1]"));
        List<WebElement> div = divData.findElements(By.xpath("./*"));
        log.info("开始:{}", div.size());
        if (div.size() > 1) {
            div.stream().forEach(l -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {

                }
                String tabindex = l.getAttribute("tabindex");
                String id = l.getAttribute("id");
                if (StringUtils.equals("-1", tabindex) && StringUtils.isNotBlank(id)) {
                    l.click();
                    WebElement button = l.findElement(By.xpath("//*[@id=\"" + id + "\"]/div[1]/div[3]/div[3]/div/div/div/button[3]"));
//                    String title = button.getAttribute("title");
//                    log.info("title:{}", title);
                    button.click();
                    log.info("删除数据成功");
                }
            });
        }
        TimeUnit.SECONDS.sleep(2);
        driver.close();

    }

    public WebDriver login() throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://mail.hengtiansoft.com/owa/auth/logon.aspx");
        TimeUnit.SECONDS.sleep(1);

        //填入邮箱名称
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys(emailName);

        //填入邮箱密码
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys(emailPassword);

        //点击登录
        TimeUnit.SECONDS.sleep(1);
        WebElement login = driver.findElement(By.className("signinTxt"));
        login.click();
        return driver;
    }

    @Test
    public void deleteEmail() throws Exception{
        WebDriver driver = login();
    }

}
