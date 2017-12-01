package me.jiangcai.poi.template;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author CJ
 */
public interface POITemplateService {

    /**
     * 导出报表
     *
     * @param out              目标流
     * @param listSupplier     直接提供List
     * @param pageFunction     提供Page；如果提供了listSupplier 则该选项就不再必要了。
     * @param equalsKey
     * @param equalsKeys       可选的唯一信息识别字段组；该字段一致的数据 将被认为数据一致
     * @param allowKeys        可选的允许可用的key
     * @param templateResource 模板资源
     * @param shellName        可选的表格名称，默认取第一个表哥
     * @throws IOException              输出错误
     * @throws IllegalTemplateException 模板有问题
     * @throws IllegalArgumentException 给的数据不行
     */
    void export(OutputStream out, Supplier<List<?>> listSupplier, Function<Pageable, Page<?>> pageFunction, EqualsKey equalsKey, Set<String> equalsKeys, Set<String> allowKeys, Resource templateResource
            , String shellName) throws IOException, IllegalTemplateException, IllegalArgumentException;

}
