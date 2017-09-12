package me.jiangcai.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author CJ
 */
@Entity
@Data
public class EntityTest1 {
    @Id
    private String id;
}
