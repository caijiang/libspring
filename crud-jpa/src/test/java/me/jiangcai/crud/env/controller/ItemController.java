package me.jiangcai.crud.env.controller;

import me.jiangcai.crud.controller.AbstractCrudController;
import me.jiangcai.crud.env.entity.Item;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.crud.row.supplier.AntDesignPaginationDramatizer;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.crud.row.supplier.Select2Dramatizer;
import me.jiangcai.crud.row.supplier.SingleRowDramatizer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/items")
public class ItemController extends AbstractCrudController<Item, Long> {
    @Override
    protected Object describeEntity(Item origin) {
        return super.describeEntity(origin);
    }

    @GetMapping("/ant-d")
    @RowCustom(distinct = true, dramatizer = AntDesignPaginationDramatizer.class)
    public Object antDesignStyle(@RequestBody(required = false) Map<String, Object> queryData) {
        return list(queryData);
    }

    @GetMapping("/jQuery")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public Object jQueryStyle(@RequestBody(required = false) Map<String, Object> queryData) {
        return list(queryData);
    }

    @GetMapping("/select2")
    @RowCustom(distinct = true, dramatizer = Select2Dramatizer.class)
    public Object select2Style(@RequestBody(required = false) Map<String, Object> queryData) {
        return list(queryData);
    }

    @Override
    protected List<FieldDefinition<Item>> listFields() {
        return Arrays.asList(
                Fields.asBasic("id"),
                Fields.asBasic("name")
        );
    }

    @Override
    protected Specification<Item> listSpecification(Map<String, Object> queryData) {
        return null;
    }

    @Override
    protected void preparePersist(Item data, Map<String, Object> otherData) {
        super.preparePersist(data, otherData);
    }
}
