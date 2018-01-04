package me.jiangcai.crud.env.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.crud.CrudFriendly;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Item implements CrudFriendly<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int amount;
}
