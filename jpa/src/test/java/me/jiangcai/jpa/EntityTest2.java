package me.jiangcai.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author CJ
 */
@Entity
public class EntityTest2 {
    private String id;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
