package me.jiangcai.poi.template;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiFunction;

/**
 * @author CJ
 */
public interface POITemplateService {

    /**
     * 导出报表
     *
     * @param out              目标流
     * @param listFunction     可以产生列表的Function,第一个参数是索引，第二个参数是长度
     * @param templateResource 模板资源
     * @param shellName        可选的表格名称，默认取第一个表哥
     * @throws IOException              输出错误
     * @throws IllegalTemplateException 模板有问题
     * @throws IllegalArgumentException 给的数据不行
     */
    void export(OutputStream out, BiFunction<Integer, Integer, Iterable<?>> listFunction, Resource templateResource
            , String shellName) throws IOException, IllegalTemplateException, IllegalArgumentException;

}