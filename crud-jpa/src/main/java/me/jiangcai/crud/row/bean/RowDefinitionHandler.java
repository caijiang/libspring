package me.jiangcai.crud.row.bean;

import me.jiangcai.crud.row.DefaultRowDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.RowDramatizer;
import me.jiangcai.crud.row.RowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author CJ
 */
@Component
public class RowDefinitionHandler implements HandlerMethodReturnValueHandler {

    private static final Log log = LogFactory.getLog(RowDefinitionHandler.class);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RowService rowService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return RowDefinition.class.isAssignableFrom(returnType.getParameterType());
    }

    //    @SuppressWarnings("unchecked")
    @SuppressWarnings("unchecked")
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {
        RowDefinition rowDefinition = (RowDefinition) returnValue;
        if (rowDefinition == null) {
            throw new IllegalStateException("null can not work for Rows.");
        }

        RowCustom rowCustom = returnType.getMethodAnnotation(RowCustom.class);

        // 看看有没有
        RowDramatizer dramatizer;
        boolean distinct;
        if (rowCustom != null) {
            try {
                dramatizer = applicationContext.getBean(rowCustom.dramatizer());
            } catch (BeansException ex) {
                dramatizer = rowCustom.dramatizer().newInstance();
            }
            distinct = rowCustom.distinct();
        } else {
            dramatizer = new DefaultRowDramatizer();
            distinct = false;
        }

        final RowDramatizer rowDramatizer = dramatizer;

        if (dramatizer.supportFetch(returnType, rowDefinition)) {
            dramatizer.fetchAndWriteResponse(returnType, rowDefinition, distinct, webRequest);
            mavContainer.setRequestHandled(true);
            return;
        }

        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        //
        final BiFunction<CriteriaBuilder, Root, List<Order>> filterFunction = (criteriaBuilder, root)
                -> rowDramatizer.order(fieldDefinitions, webRequest, criteriaBuilder, root);

        if (rowCustom != null && rowCustom.fetchAll()) {
            List<?> list = rowService.queryFields(rowDefinition, distinct, filterFunction);
            dramatizer.writeResponse(list.size(), list, fieldDefinitions, webRequest);
        } else {
            final int startPosition = dramatizer.queryOffset(webRequest);
            final int size = dramatizer.querySize(webRequest);

            Page<?> page = rowService.queryFields(rowDefinition, distinct,
                    filterFunction
                    , new PageRequest(startPosition / size, size));
            dramatizer.writeResponse(page.getTotalElements(), page.getContent(), fieldDefinitions, webRequest);
        }
        mavContainer.setRequestHandled(true);

    }

}
