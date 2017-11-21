package me.jiangcai.poi.template.service;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
public class Cell {
    public static final Cell EMPTY = new Cell(null, 0, 0);
    private final Object value;
    private int rows;
    private int cols;
}
