package org.example;

import java.util.stream.IntStream;

public class MatrixMultiplicationParallelStreams {

    public double[][] execute(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        IntStream.range(0, n).parallel().forEach(i -> {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        });
        return c;
    }
}
