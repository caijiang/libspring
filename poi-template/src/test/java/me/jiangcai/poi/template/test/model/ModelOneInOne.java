package me.jiangcai.poi.template.test.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Data
public class ModelOneInOne {
    private String name;
    private int amount;
    private BigDecimal price;
}
