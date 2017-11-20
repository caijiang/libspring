package me.jiangcai.poi.template.test.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Data
public class ModelOne {
    private List<ModelOneInOne> goods;
    private String name;
    private String address;
    private String mobile;
    private String payMethod;
    private String orderId;
    private LocalDateTime orderDate;
}
