package me.jiangcai.lib.jdbc.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author CJ
 */
@Entity
public class TestData {

    @Id
    private String id;

    private String name1;

}
