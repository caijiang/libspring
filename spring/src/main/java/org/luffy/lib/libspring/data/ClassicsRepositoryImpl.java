package org.luffy.lib.libspring.data;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Created by luffy on 2015/6/17.
 * @deprecated 使用 {@link me.jiangcai.lib.spring.data.ClassicsRepositoryImpl}代替
 * @author luffy luffy.ja at gmail.com
 */
public class ClassicsRepositoryImpl<T, ID extends Serializable>
        extends me.jiangcai.lib.spring.data.ClassicsRepositoryImpl<T, ID> implements ClassicsRepository<T> {

    public ClassicsRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public ClassicsRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }
}
