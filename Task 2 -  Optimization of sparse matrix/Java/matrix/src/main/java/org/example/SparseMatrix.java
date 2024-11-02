package org.example;

import java.util.HashMap;
import java.util.Map;

public class SparseMatrix {
    final Map<Integer, Map<Integer, Double>> matrix;
    private final int rows;
    private final int cols;

    public SparseMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new HashMap<>();
    }

    public void set(int row, int col, double value) {
        if (value != 0) {
            matrix.computeIfAbsent(row, k -> new HashMap<>()).put(col, value);
        }
    }

    public Double get(int row, int col) {
        return matrix.getOrDefault(row, new HashMap<>()).get(col);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
