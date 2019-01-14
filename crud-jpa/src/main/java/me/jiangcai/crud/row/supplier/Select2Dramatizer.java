package me.jiangcai.crud.row.supplier;

import me.jiangcai.crud.row.AbstractMediaRowDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowDramatizer;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://select2.github.io/options.html
 * 默认size = 30
 *
 * @author CJ
 */
public class Select2Dramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaQuery query, CriteriaBuilder criteriaBuilder, Root root) {
        return null;
    }

    @Override
    public String getOffsetParameterName() {
        throw new IllegalStateException("never!!");
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        int page = readAsInt(webRequest, "page", 1);
        return (page - 1) * querySize(webRequest);
    }

    @Override
    public int getDefaultSize() {
        return 30;
    }

    @Override
    public String getSizeParameterName() {
        return "size";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest) throws IOException {
        // 是否已完成？ total < ofset+size
        Map<String, Object> json = new HashMap<>();
        json.put("total_count", page.getTotalElements());
        json.put("items", rows);
        // 实际上……
//  "incomplete_results": false,
//        json.put("incomplete_results", total < queryOffset(webRequest) + querySize(webRequest));

        objectMapper.writeValue(webRequest.getNativeResponse(HttpServletResponse.class).getOutputStream(), json);
    }
}
