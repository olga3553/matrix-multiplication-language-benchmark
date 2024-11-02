package org.example;

import java.util.HashMap;
import java.util.Map;

public class SparseMatrixMultiplication {
    public SparseMatrix execute(SparseMatrix a, SparseMatrix b) {
        int n = a.getRows();
        SparseMatrix c = new SparseMatrix(n, n);

        for (int i = 0; i < n; i++) {
            Map<Integer, Double> rowA = a.matrix.get(i);
            if (rowA != null) {
                for (Map.Entry<Integer, Double> entryA : rowA.entrySet()) {
                    int k = entryA.getKey();
                    double aValue = entryA.getValue();

                    Map<Integer, Double> colB = b.matrix.get(k);
                    if (colB != null) {
                        for (Map.Entry<Integer, Double> entryB : colB.entrySet()) {
                            int j = entryB.getKey();
                            double bValue = entryB.getValue();

                            double currentValue = c.get(i, j) != null ? c.get(i, j) : 0.0;
                            c.set(i, j, currentValue + aValue * bValue);
                        }
                    }
                }
            }
        }
        return c;
    }
}
