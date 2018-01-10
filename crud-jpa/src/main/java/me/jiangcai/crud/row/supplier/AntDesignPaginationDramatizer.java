package me.jiangcai.crud.row.supplier;

import me.jiangcai.crud.row.AbstractMediaRowDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowDramatizer;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://ant.design/components/pagination-cn/
 * 数据将渲染在list,pagination中
 * order的标准暂不明确
 *
 * @author CJ
 * @since 4.0
 */
public class AntDesignPaginationDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {
    @Override
    public String getOffsetParameterName() {
        throw new IllegalStateException("never!!");
    }

    @Override
    public int getDefaultSize() {
        return 10;
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        int page = readAsInt(webRequest, "current", 1);
        return (page - 1) * querySize(webRequest);
    }

    @Override
    public String getSizeParameterName() {
        return "pageSize";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest) throws IOException {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page.getNumber() + 1);
        pagination.put("pageSize", page.getSize());
        pagination.put("total", page.getTotalElements());

        Map<String, Object> json = new HashMap<>();
        json.put("pagination", pagination);
        json.put("list", rows);
        objectMapper.writeValue(webRequest.getNativeResponse(HttpServletResponse.class).getOutputStream(), json);
    }

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder, Root root) {
        return null;
    }
}
