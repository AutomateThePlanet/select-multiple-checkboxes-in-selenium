import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

public class SelectCheckboxesLambdaTestTests {
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 30;
    private WebDriver driver;
    private WebDriverWait webDriverWait;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() throws MalformedURLException {
        String username = System.getenv("LT_USERNAME");
        String authkey = System.getenv("LT_ACCESSKEY");
        String hub = "@hub.lambdatest.com/wd/hub";

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        HashMap<String, Object> ltOptions = new HashMap<String, Object>();
        ltOptions.put("user", username);
        ltOptions.put("accessKey", authkey);
        ltOptions.put("build", "Selenium 4");
        ltOptions.put("name",this.getClass().getName());
        ltOptions.put("platformName", "Windows 10");
        ltOptions.put("seCdp", true);
        ltOptions.put("selenium_version", "4.0.0");
        capabilities.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), capabilities);
        driver.manage().window().maximize();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_FOR_ELEMENT_TIMEOUT));
    }

    @Test
    public void testSingleCheckbox()
    {
        driver.get("https://www.lambdatest.com/selenium-playground/checkbox-demo");

        WebElement checkbox = driver.findElement(By.id("isAgeSelected"));

        Assertions.assertTrue(checkbox.isDisplayed());
        checkbox.click();

        Assertions.assertTrue(checkbox.isEnabled());
        Assertions.assertTrue(checkbox.isSelected());

        webDriverWait.until(ExpectedConditions.textToBe(By.id("txtAge"), "Success - Check box is checked"));
    }


    @Test
    public void testMultipleCheckbox()
    {
        driver.get("https://www.lambdatest.com/selenium-playground/checkbox-demo");

        List<WebElement> checkboxes = driver.findElements(By.className("cb-element mr-10"));

        for(int i=0; i<checkboxes.size(); i++)
        {
            if(checkboxes.get(i).isDisplayed() && checkboxes.get(i).isEnabled())
            {
                checkboxes.get(i).click();
            }
        }

        checkboxes.stream().forEach(c -> { if(c.isDisplayed() && c.isEnabled()) c.click(); });

        checkboxes.get(1).click();

        Assertions.assertFalse(checkboxes.get(1).isSelected());
    }

    @Test
    public void testCheckboxesInTable()
    {
        driver.get("https://www.lambdatest.com/selenium-playground/table-records-filter-demo");

        // when check input is not working, we need to click the div
        var checkbox = driver.findElement(
                By.xpath("//span[contains(text(), 'Green')]/parent::h4/parent::div/parent::div/parent::td/preceding-sibling::td/div"));

        var textColumn = driver.findElement(By.xpath("//span[contains(text(), 'Green')]"));
        var checkbox1 = driver.findElement(with(By.tagName("div")).toLeftOf(textColumn));
        checkbox.click();
        checkbox1.click();

        Assertions.assertTrue(checkbox.isEnabled());
        Assertions.assertFalse(checkbox.isSelected());
    }

    @Test
    public void testTreeViewCheckBoxes()
    {
        driver.get("https://www.grapecity.com/componentone/demos/aspnet/ControlExplorer/C1TreeView/CheckBox.aspx");

        // when check input is not working, we need to click the div
//        var checkbox = driver.findElement(
//                By.xpath("//span[contains(text(), 'Folder 1.1')]/parent::h4/parent::div/parent::div/parent::td/preceding-sibling::td/div"));

        var checkboxLabel = driver.findElement(By.xpath("//span[contains(text(), 'Folder 2')]"));
        var checkbox = driver.findElement(with(By.tagName("div")).toLeftOf(checkboxLabel));

        checkbox.click();

        //var openSectionButton = driver.findElement(with(By.xpath(".//span[@class='ui-icon ui-icon-triangle-1-e']")).toLeftOf(checkbox));
        var openSectionButton = driver.findElement(By.xpath("//span[contains(text(), 'Folder 2')]/parent::a/preceding-sibling::span"));
        openSectionButton.click();

        Assertions.assertTrue(checkbox.isEnabled());
        Assertions.assertFalse(checkbox.isSelected());
    }

    private void openSections(List<String> sectionsToOpen, List<String> checkBoxesToCheck) {
        for (var currentNodeToOpen:sectionsToOpen) {
            var nodeLocator = String.format("//span[contains(text(), '%s')]/parent::a/preceding-sibling::span", currentNodeToOpen);
            var openSectionButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(nodeLocator)));
            openSectionButton.click();
        }

        for (var currentNodeToClick:checkBoxesToCheck) {
            // it might not be unique
            var nodeLocator = String.format("//span[contains(text(), '%s')]", currentNodeToClick);
            var nodeToClick = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(nodeLocator)));
            nodeToClick.click();
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}