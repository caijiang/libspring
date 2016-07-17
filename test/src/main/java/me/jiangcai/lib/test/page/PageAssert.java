package me.jiangcai.lib.test.page;

/**
 * @author CJ
 * @since 2.2
 */
class PageAssert extends AbstractPageAssert<PageAssert, AbstractPage> {
    protected PageAssert(AbstractPage actual) {
        super(actual, PageAssert.class);
    }
}
