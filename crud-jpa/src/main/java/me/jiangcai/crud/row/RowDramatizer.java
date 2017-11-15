package me.jiangcai.crud.row;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.List;

/**
 * 数据重新定制者
 *
 * @author CJ
 */
public interface RowDramatizer {

    /**
     * @param fields          要显示的字段
     * @param webRequest      请求
     * @param criteriaBuilder cb
     * @param root            root
     * @return 排序规则;可以返回null表示不支持排序
     */
    List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder
            , Root root);

    /**
     * @param webRequest 请求
     * @return 开始查询位；默认0
     */
    int queryOffset(NativeWebRequest webRequest);

    /**
     * @param webRequest 请求
     * @return 查询长度
     */
    int querySize(NativeWebRequest webRequest);

    /**
     * 写入响应
     *
     * @param total      总数
     * @param list       结果集
     * @param fields     字段
     * @param webRequest 请求
     * @throws IOException 写入时出现的
     */
    void writeResponse(long total, List<?> list, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest) throws IOException;

    /**
     * @param type          MVC参数类型
     * @param rowDefinition 相关定义
     * @return 是否支持自行获取数据
     */
    default boolean supportFetch(MethodParameter type, RowDefinition rowDefinition) {
        return false;
    }

    /**
     * 完成数据获取并且写入到响应流
     *
     * @param type          MVC参数类型
     * @param rowDefinition 相关定义
     * @param distinct      distinct
     * @param webRequest    请求
     * @throws IOException 写入时出现的
     */
    default void fetchAndWriteResponse(MethodParameter type, RowDefinition rowDefinition, boolean distinct
            , NativeWebRequest webRequest) throws IOException {

    }
}
