package me.jiangcai.lib.test.page;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import me.jiangcai.lib.test.SpringWebTest;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 页面的基类
 *
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractPage {

    private static final Field elementField;

    static {
        try {
            elementField = HtmlUnitWebElement.class.getDeclaredField("element");
            elementField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new InternalError("炸!,版本更新了?", e);
        }
    }

    protected final WebDriver webDriver;
    /**
     * 操作该页面的测试实例
     */
    private SpringWebTest TestInstance;

    public AbstractPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * 你懂的
     *
     * @param element
     * @return
     * @throws IOException
     * @since 3.0
     */
    public static BufferedImage toImage(WebElement element) throws IOException {
        try {
            HtmlImage image = (HtmlImage) elementField.get(element);
            return image.getImageReader().read(0);
        } catch (IllegalAccessException e) {
            throw new InternalError("炸!,版本更新了?", e);
        }
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
        inputSelect(formElement, inputName, label::equals);
    }

    /**
     * 从表单的可选项中选择一个select的选项
     * <p>
     * 支持chosen-select,但未经严格测试</p>
     *
     * @param formElement 指定表单
     * @param inputName   select 名字
     * @param useIt       是否使用这个label
     * @since 2.2
     */
    public void inputSelect(WebElement formElement, String inputName, Function<String, Boolean> useIt) {
        WebElement input = formElement.findElement(By.name(inputName));

        if (input.getAttribute("class") != null && input.getAttribute("class").contains("chosen-select")) {
            // 换一个方式
            WebElement container = formElement.findElements(By.className("chosen-container"))
                    .stream()
                    .filter(webElement -> webElement.getAttribute("title") != null && webElement.getAttribute("title")
                            .equals(input.getAttribute("title")))
                    .findAny().orElse(null);
            if (container != null) {
                container.click();
                // TODO 还是不完善的 基本可用 要是数据不是太多的话。
                for (WebElement element : container.findElements(By.cssSelector("li.active-result"))) {
                    if (useIt.apply(element.getText())) {
                        element.click();
                        return;
                    }
                }
                throw new IllegalStateException("找不到符合要求的Label");
            }
        }
        //chosen-container chosen-container-single and same title
        // li.active-result

//        input.clear();
        for (WebElement element : input.findElements(By.tagName("option"))) {
//            System.out.println(element.getText());
            if (useIt.apply(element.getText())) {
                element.click();
                return;
            }
        }
        throw new IllegalStateException("找不到符合要求的Label");
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
     * 断言标题吻合
     *
     * @param title 期望标题
     * @since 3.0
     */
    protected void assertTitle(String title) {
        assertThat(webDriver.getTitle())
                .describedAs("期望标题为" + title)
                .isEqualToIgnoringCase(title);
    }

    /**
     * 生成一个新的页面
     *
     * @param pageClass 新页面类型
     * @param webDriver 特定webDriver
     * @param <T>       新页面类型
     * @return 新页面实例
     * @since 3.0
     */
    protected <T extends AbstractPage> T initPage(Class<T> pageClass, WebDriver webDriver) {
        T page = PageFactory.initElements(webDriver, pageClass);
//        page.setResourceService(resourceService);
        page.setTestInstance(getTestInstance());
        page.validatePage();
        return page;
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
        return initPage(pageClass, webDriver);
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

    /**
     * @param element 通过JS调用方式让特定element可见
     */
    public void makeVisible(WebElement element) {
        String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
        ((JavascriptExecutor) webDriver).executeScript(js, element);
    }

    /**
     * 若目标存在且可见，则点击直至它消失
     *
     * @param by 目标选择器
     */
    public void clickTargetForClosePurpose(By by) {
        webDriver.findElements(by).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> {
                    element.click();
                    new WebDriverWait(webDriver, 3)
                            .until(ExpectedConditions.invisibilityOfElementLocated(by));
                });
    }

    ///////////////////LAYER UI////////////////

    /**
     * 会自动等待2s为了 等消息的出现；即使不出现也不会报错
     *
     * @return 断言弹出的消息
     */
    public AbstractCharSequenceAssert<?, String> assertLayerMessage() {
        try {
            WebDriverUtil.waitFor(webDriver, driver -> driver.findElements(By.className("layui-layer-content")).stream()
                    .filter(webElement -> !webElement.getAttribute("class").contains("layui-layer-loading"))// 不可包含 layui-layer-loading
                    .filter(WebElement::isDisplayed).count() > 0, 2);
        } catch (TimeoutException ignored) {
        }
        WebElement target = webDriver.findElements(By.className("layui-layer-content")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst().orElse(null);
        if (target == null)
            return assertThat((String) null);
        return assertThat(target.getText());
    }

    /**
     * 检查下是否有弹出框，有的话function就会被执行
     *
     * @param function 参数分别为弹出窗标题，整个弹出界面的div；如果返回true则表示输入，返回false就直接关闭
     */
    public void layerPrompt(BiFunction<String, WebElement, Boolean> function) {
        final By locator = By.className("layui-layer-prompt");
        layerInputAndYes(function, locator);
    }

    /**
     * 检查下是否有弹出框，有的话function就会被执行
     *
     * @param function 参数分别为弹出窗标题，整个弹出界面的div；如果返回true则表示输入，返回false就直接关闭
     */
    public void layerDialog(BiFunction<String, WebElement, Boolean> function) {
        final By locator = By.className("layui-layer-dialog");
        layerInputAndYes(function, locator);
    }

    private void layerInputAndYes(BiFunction<String, WebElement, Boolean> function, By locator) {
        // layui-layer-dialog
        try {
            new WebDriverWait(webDriver, 1)
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            WebElement div = webDriver.findElements(locator).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);
            if (function.apply(div.findElement(By.className("layui-layer-title")).getText(), div)) {
                clickTargetForClosePurpose(By.className("layui-layer-btn0"));
            } else {
                clickTargetForClosePurpose(By.className("layui-layer-btn1"));
                clickTargetForClosePurpose(By.className("layui-layer-close"));
            }
        } catch (TimeoutException ignored) {
        }
    }

    ///////////////////LAYER UI////////////////

    //////////// select2 /////

    /**
     * 使用select2选择器
     *
     * @param selectSelector 相关select的cssSelector
     * @param input          输入内容
     * @param predicate      选择依赖
     */
    public void select2For(String selectSelector, String input, Predicate<WebElement> predicate) {
        final By resultsBy = By.className("select2-results__option");
        final By containerBy = By.className("select2-container--open");
        final By inputBy = By.cssSelector(".select2-search > input");
        final By loadMoreBy = By.className("select2-results__option--load-more");
        // 如果当前就打开了一个 那就不好弄了！
        if (webDriver.findElements(containerBy).stream().filter(WebElement::isDisplayed).count() > 0)
            throw new IllegalStateException("当前就打开了一个select2 选择器；点击其他地方关闭这个选择器的功能尚未开发呢。");


        WebElement selectHelper = webDriver.findElement(By.cssSelector(selectSelector + " + .select2-container"));
        selectHelper.findElement(By.className("select2-selection")).click();
        new WebDriverWait(webDriver, 2).until(ExpectedConditions.visibilityOfElementLocated(containerBy));

        WebElement inputElement = webDriver.findElement(inputBy);
        inputElement.clear();
        for (char c : input.toCharArray()) {
            inputElement.sendKeys("" + c);
//            printThisPage();
        }
//        inputElement.sendKeys(input);
        // 等待刷新
        WebDriverUtil.waitFor(webDriver, driver -> driver
                .findElements(resultsBy)
                .stream()
                .anyMatch(webElement -> !webElement.getAttribute("class").contains("loading-results")), 2);

        // select2-results__option--load-more
        int count = 0;
        while (count++ < 100) {
            WebElement target = webDriver.findElements(resultsBy).stream()
                    .filter(predicate)
                    .findFirst()
                    .orElse(null);
            if (target != null) {
                target.click();
                return;
            }
            //寻找load more
//            new Actions(webDriver).
//            webDriver.findElement(loadMoreBy).click();
//            new WebDriverWait(webDriver,1).until(ExpectedConditions.visibilityOfElementLocated(loadMoreBy));
//            精准的做法是将它满满滚动
        }
//        webDriver.findElements(resultsBy).stream()
//                .filter(predicate)
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("输入了" + input + "但没有找到期望的可选择结果"))
//                .click();
        throw new IllegalStateException("输入了" + input + "但没有找到期望的可选择结果");
    }
    /// select2 end///

}
