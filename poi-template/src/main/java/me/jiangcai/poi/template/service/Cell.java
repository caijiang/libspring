package me.jiangcai.poi.template.service;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
public class Cell {
    public static Cell empty(){
        return new Cell(null, 0, 0);
    }
    private Object value;
    private int rows;
    private int cols;
}
