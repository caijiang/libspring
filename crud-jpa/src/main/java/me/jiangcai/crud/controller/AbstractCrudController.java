package me.jiangcai.crud.controller;

import me.jiangcai.crud.CrudFriendly;
import me.jiangcai.crud.exception.CrudNotFoundException;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * ID可能并不都是可简单序列化的，所以MVC本身需要支撑它们的序列化，这个由客户端项目实现。
 * 渲染整个entity？会不会出事呢……
 * TODO 安全控制
 * TODO PUT 修改
 *
 * @author CJ
 * @since 1.8
 */
public abstract class AbstractCrudController<T extends CrudFriendly<ID>, ID extends Serializable> {

    @Autowired
    private EntityManager entityManager;

    @GetMapping(value = "/{id}")
    @Transactional(readOnly = true)
    @ResponseBody
    public Object getOne(@PathVariable ID id) {
        Class<T> type = currentClass();
        T entity = entityManager.find(type, id);
        if (entity == null) {
            throw new CrudNotFoundException();
        }
        return describeEntity(entity);
    }

    //增加一个数据
    @PostMapping
    @Transactional
    public ResponseEntity addOne(@RequestBody T postData, @RequestBody(required = false) Map<String, Object> otherData) throws URISyntaxException {
        preparePersist(postData, otherData);
        entityManager.persist(postData);
        entityManager.flush();
        ID id = postData.getId();
        // TODO 串化讲道理应该是通过MVC配置获取，这里先简单点来
        return ResponseEntity
                .created(new URI(homeUri() + "/" + id))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteOne(@PathVariable ID id) {
        T entity = entityManager.find(currentClass(), id);
        if (entity == null)
            throw new CrudNotFoundException();
        entityManager.remove(entity);
    }

    // 获取数据
    @GetMapping
    @RowCustom(distinct = true)
    public RowDefinition<T> list(@RequestBody(required = false) Map<String, Object> queryData) {
        return new RowDefinition<T>() {
            @Override
            public Class<T> entityClass() {
                return currentClass();
            }

            @Override
            public List<FieldDefinition<T>> fields() {
                return listFields();
            }

            @Override
            public Specification<T> specification() {
                return listSpecification(queryData);
            }
        };
    }

    /**
     * @return 展示用的
     */
    protected abstract List<FieldDefinition<T>> listFields();

    /**
     * @param queryData 查询时提交的数据
     * @return 查询规格
     * @see RowDefinition#specification()
     */
    protected abstract Specification<T> listSpecification(Map<String, Object> queryData);

    /**
     * 准备持久化
     *
     * @param data      准备持久化的数据
     * @param otherData 其他提交的数据
     */
    protected void preparePersist(T data, Map<String, Object> otherData) {

    }

    private String homeUri() {
        return getClass().getAnnotation(RequestMapping.class).value()[0];
    }

    /**
     * @param origin entity对象，切勿改变原始entity对象
     * @return 描述这个对象
     */
    protected Object describeEntity(T origin) {
        return origin;
    }

    @SuppressWarnings("unchecked")
    private Class<T> currentClass() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
