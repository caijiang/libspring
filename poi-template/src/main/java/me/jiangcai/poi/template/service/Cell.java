package me.jiangcai.poi.template.service;

import lombok.Data;

/**
 * @author CJ
 */
@Data
public class Cell {
    public static final Cell EMPTY = new Cell(0, 0, null);
    private final int rows;
    private final int cols;
    private final Object value;
}
