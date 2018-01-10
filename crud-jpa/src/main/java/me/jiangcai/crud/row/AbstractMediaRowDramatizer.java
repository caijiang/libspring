package me.jiangcai.crud.row;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.NumberUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * 直接以一种正文响应写入
 *
 * @author CJ
 */
public abstract class AbstractMediaRowDramatizer implements RowDramatizer {
    protected int readAsInt(NativeWebRequest webRequest, String name, int defaultValue) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter(name), Integer.class);
        } catch (Exception ignored) {

            return defaultValue;
        }
    }


    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        return readAsInt(webRequest, getOffsetParameterName(), 0);
    }

    public abstract String getOffsetParameterName();

    public abstract int getDefaultSize();

    public abstract String getSizeParameterName();

    public abstract MediaType toMediaType();

    @Override
    public int querySize(NativeWebRequest webRequest) {
        int size = readAsInt(webRequest, getSizeParameterName(), getDefaultSize());
        if (size <= 0)
            size = 10000;
        return size;
    }

    @Override
    public void writeResponse(Page<?> page, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest) throws IOException {
        final HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);

        nativeResponse.setHeader("Content-Type", toMediaType().toString());

        List<Object> rows = drawToRows(page.getContent(), fields);

        writeData(page, rows, webRequest);
    }

    protected abstract void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest) throws IOException;


    private List<Object> drawToRows(List<?> list, List<? extends IndefiniteFieldDefinition> fields) {
        List<Object> rows = new ArrayList<>();
        Function<List, ?> function = (input) -> drawToRows(input, fields);
        for (Object data : list) {
// data 通常为一个Object[] 然后fields逐个描述它
            HashMap<String, Object> outData = new HashMap<>();
            if (data.getClass().isArray()) {
                assert Array.getLength(data) == fields.size();
                for (int i = 0; i < fields.size(); i++) {
                    IndefiniteFieldDefinition fieldDefinition = fields.get(i);
                    outData.put(fieldDefinition.name(), fieldDefinition.export(Array.get(data, i), toMediaType(), function));
                }
            } else {
                // 只有一个结果？
                for (IndefiniteFieldDefinition fieldDefinition : fields) {
                    outData.put(fieldDefinition.name(), fieldDefinition.export(data, toMediaType(), function));
                }
            }

            rows.add(outData);
        }
        return rows;
    }
}
