package me.jiangcai.lib.test.page;

import com.gargoylesoftware.htmlunit.html.HtmlInput;
import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 页面的基类
 *
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractPage {

    protected final WebDriver webDriver;
    /**
     * 操作该页面的测试实例
     */
    private SpringWebTest TestInstance;

    public AbstractPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public SpringWebTest getTestInstance() {
        return TestInstance;
    }

    public void setTestInstance(SpringWebTest testInstance) {
        TestInstance = testInstance;
    }

    /**
     * 验证该页面
     */
    public abstract void validatePage();

    /**
     * @return 断言
     * @since 2.2
     */
    public AbstractPageAssert<?, ? extends AbstractPage> assertMe() {
        return AbstractPageAssert.assertThat(this);
    }

    /**
     * 在页面操作试图获取{@link #webDriver}之前必须先调用这个方法
     * <p>
     * 默认实现是什么都不做
     *
     * @since 2.2
     */
    public void beforeDriver() {

    }

    /**
     * 打印这个页面
     *
     * @param printStream 输出流
     * @since 2.2
     */
    public void printThisPage(PrintStream printStream) {
        beforeDriver();
        printStream.println("url:" + webDriver.getCurrentUrl());
        printStream.println("page:" + this);
        printStream.println(webDriver.getPageSource());
    }

    /**
     * 打印这个页面
     *
     * @since 2.2
     */
    public void printThisPage() {
        printThisPage(System.err);
    }


    /**
     * 从表单的可选项中选择一个select的选项
     * <p>
     * 支持chosen-select,但未经严格测试</p>
     *
     * @param formElement 指定表单
     * @param inputName   select 名字
     * @param label       option的label
     * @since 2.2
     */
    public void inputSelect(WebElement formElement, String inputName, String label) {
        WebElement input = formElement.findElement(By.name(inputName));

        if (input.getAttribute("class") != null && input.getAttribute("class").contains("chosen-select")) {
            // 换一个方式
            WebElement container = formElement.findElements(By.className("chosen-container"))
                    .stream()
                    .filter(webElement -> webElement.getAttribute("title") != null && webElement.getAttribute("title")
                            .equals(input.getAttribute("title")))
                    .findAny().orElseThrow(() -> new IllegalStateException("使用了chosen-select,但没看到chosen-container"));

            container.click();
            // TODO 还是不完善的 基本可用 要是数据不是太多的话。
            for (WebElement element : container.findElements(By.cssSelector("li.active-result"))) {
                if (label.equals(element.getText())) {
                    element.click();
                    return;
                }
            }
            throw new IllegalStateException("找不到" + label);
        }
        //chosen-container chosen-container-single and same title
        // li.active-result

        input.clear();
        for (WebElement element : input.findElements(By.tagName("option"))) {
//            System.out.println(element.getText());
            if (label.equals(element.getText())) {
                element.click();
                return;
            }
        }
        throw new IllegalStateException("找不到" + label);
    }

    /**
     * 输入tags
     *
     * @param formElement 指定表单
     * @param inputName   text name
     * @param values      值
     * @since 2.2
     */
    public void inputTags(WebElement formElement, String inputName, String[] values) {
        WebElement input = formElement.findElement(By.name(inputName));
        input.clear();
        String id = input.getAttribute("id");
        // 规律是加上 _tag
        WebElement toInput = formElement.findElement(By.id(id + "_tag"));
        for (String value : values) {
            toInput.clear();
            toInput.sendKeys(value);
            toInput.sendKeys(Keys.ENTER);
        }
    }

    /**
     * 在指定表单中输入值type=text
     *
     * @param formElement 表单Element
     * @param inputName   input的name
     * @param value       要输入的值
     * @since 2.2
     */
    public void inputText(WebElement formElement, String inputName, String value) {
        try {
            WebElement input = formElement.findElement(By.name(inputName));
            input.clear();
            if (value != null)
                input.sendKeys(value);
        } catch (ElementNotVisibleException exception) {
            printThisPage();
            throw exception;
        }

    }

    boolean inputChecked(WebElement input) {
        String checked = input.getAttribute("checked");
        if (checked == null)
            return false;
        return checked.equalsIgnoreCase("checked") || checked.equalsIgnoreCase("true");
    }

    /**
     * 输入一个checkbox
     *
     * @param form    表单Element
     * @param name    input的name
     * @param checked 选择与否
     * @since 2.2
     */
    public void inputChecked(WebElement form, String name, boolean checked) {
        WebElement input = form.findElement(By.name(name));
        if (checked && inputChecked(input))
            return;
        if (!checked && !inputChecked(input))
            return;
        input.click();
    }

    /**
     * 输入一个隐藏的html,将道理这是用户端无法做到的事,但为了技术上的便利还是添加了;不推荐使用
     *
     * @param form  表单
     * @param name  input name
     * @param value avlue
     * @since 2.2
     */
    public void inputHidden(WebElement form, String name, String value) {
        WebElement input = form.findElement(By.name(name));
        try {
            Field field = HtmlUnitWebElement.class.getDeclaredField("element");
            field.setAccessible(true);
            HtmlInput htmlHiddenInput = (HtmlInput) field.get(input);
            if (value == null)
                htmlHiddenInput.setValueAttribute("");
            else
                htmlHiddenInput.setValueAttribute(value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 以当前{@link #webDriver}生成一个新的页面
     *
     * @param pageClass 新页面类型
     * @param <T>       新页面类型
     * @return 新页面实例
     * @since 2.2
     */
    protected <T extends AbstractPage> T initPage(Class<T> pageClass) {
        T page = PageFactory.initElements(webDriver, pageClass);
//        page.setResourceService(resourceService);
        page.setTestInstance(getTestInstance());
        page.validatePage();
        return page;
    }

    /**
     * 刷新当前页面,跟浏览器刷新是一致的,并在完成之后调用验证
     */
    public void refresh() {
        beforeDriver();
        webDriver.navigate().refresh();
        PageFactory.initElements(webDriver, this);
        validatePage();
    }

    /**
     * 重新载入当前逻辑页面信息,并在完成之后调用验证
     */
    public void reloadPageInfo() {
        beforeDriver();
        PageFactory.initElements(webDriver, this);
        validatePage();
    }

    /**
     * 等待,只到当前页面符合指定条件
     * <p>
     * 完成后会调用 {@link #reloadPageInfo() 刷新}
     * </p>
     *
     * @param waitConsumer 条件给予者
     */
    public void waitOn(Consumer<FluentWait<WebDriver>> waitConsumer) throws InterruptedException {
        Thread.sleep(500L);
        beforeDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.ignoring(NoSuchElementException.class);
        waitConsumer.accept(wait);
        reloadPageInfo();
    }


}
