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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

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
    public void writeResponse(long total, List<?> list, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest) throws IOException {

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
            poiTemplateService.export(outputStream, (integer, integer2)
                    -> rowService.queryEntity(rowDefinition, new PageRequest(integer, integer2)), resource, null);

            outputStream.flush();
        } catch (IllegalTemplateException e) {
            throw new IllegalArgumentException(e);
        }

        response.setStatus(200);
    }
}
