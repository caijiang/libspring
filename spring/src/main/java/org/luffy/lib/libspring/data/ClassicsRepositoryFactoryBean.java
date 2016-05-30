package org.luffy.lib.libspring.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

/**
 * Created by luffy on 2015/6/17.
 * @deprecated 使用 {@link me.jiangcai.lib.spring.data.ClassicsRepositoryFactoryBean}代替
 *
 * @author luffy luffy.ja at gmail.com
 */
public class ClassicsRepositoryFactoryBean<R extends JpaRepository<T, I>, T,
        I extends Serializable> extends me.jiangcai.lib.spring.data.ClassicsRepositoryFactoryBean<R, T, I> {

}