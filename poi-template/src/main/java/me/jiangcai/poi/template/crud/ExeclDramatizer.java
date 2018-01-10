package me.jiangcai.poi.template.crud;

import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.IndefiniteFieldDefinition;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.RowDramatizer;
import me.jiangcai.crud.row.RowService;
import me.jiangcai.poi.template.IllegalTemplateException;
import me.jiangcai.poi.template.POITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Component
public class ExeclDramatizer implements RowDramatizer {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private POITemplateService poiTemplateService;
    @Autowired
    private RowService rowService;

    private static List<?> export(List<?> page, RowDefinition<?> rowDefinition) {
        final List<? extends FieldDefinition<?>> fields = rowDefinition.fields();
        ArrayList<Object> list = new ArrayList<>();
        for (Object aPage : page) {
            Object[] objects = (Object[]) aPage;
            HashMap<String, Object> data = new HashMap<>();

            for (int j = 0; j < fields.size(); j++) {
                data.put(fields.get(j).name(), objects[j]);
            }
            list.add(data);
        }
        return list;
    }

    private static Page<?> export(Page<?> page, RowDefinition<?> rowDefinition) {
        final List<? extends FieldDefinition<?>> fields = rowDefinition.fields();
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 0; i < page.getContent().size(); i++) {
            Object[] objects = (Object[]) page.getContent().get(i);
            HashMap<String, Object> data = new HashMap<>();

            for (int j = 0; j < fields.size(); j++) {
                data.put(fields.get(j).name(), objects[j]);
            }
            list.add(data);
        }
        return new PageImpl<>(list, null, page.getTotalElements());
    }

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaBuilder criteriaBuilder, Root root) {
        return null;
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        return 0;
    }

    @Override
    public int querySize(NativeWebRequest webRequest) {
        return 0;
    }

    @Override
    public void writeResponse(Page<?> page, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest) throws IOException {

    }

    @Override
    public boolean supportFetch(MethodParameter type, RowDefinition rowDefinition) {
        return type.hasMethodAnnotation(ExeclReport.class);
    }

    @Override
    public void fetchAndWriteResponse(MethodParameter type, RowDefinition rowDefinition
            , boolean distinct, NativeWebRequest webRequest) throws IOException {
        ExeclReport report = type.getMethodAnnotation(ExeclReport.class);

        Resource resource = applicationContext.getResource(report.value());

        if (!resource.exists())
            throw new IllegalArgumentException(report.value() + " do not exists.");

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        response.setContentType("application/vnd.ms-excel");
        final String name;
        if (rowDefinition.getName() != null)
            name = rowDefinition.getName();
        else
            name = report.value();
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name + ".xls", "UTF-8"));

        final ServletOutputStream outputStream = response.getOutputStream();
        try {
            poiTemplateService.export(outputStream, () -> {
                        if (CollectionUtils.isEmpty(rowDefinition.fields()))
                            return rowService.queryAllEntity(rowDefinition);
                        return export(rowService.queryFields(rowDefinition, distinct, null), rowDefinition);
                    }, null, null, report.equalsKeys().length == 0 ? null : Stream.of(report.equalsKeys()).collect(Collectors.toSet())
                    , report.allowKeys().length == 0 ? null : Stream.of(report.allowKeys()).collect(Collectors.toSet())
                    , resource, null);

            outputStream.flush();
        } catch (IllegalTemplateException e) {
            throw new IllegalArgumentException(e);
        }

        response.setStatus(200);
    }

}
