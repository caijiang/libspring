package me.jiangcai.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author CJ
 */
@Entity
public class EntityTest2 {
    private String id2;

    @Id
    public String getId() {
        return id2;
    }

    public void setId(String id) {
        this.id2 = id;
    }
}
