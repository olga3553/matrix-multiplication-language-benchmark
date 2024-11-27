package org.example;

import java.util.stream.IntStream;

public class StreamMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        double[][] result = new double[rowsA][colsB];

        IntStream.range(0, rowsA).parallel().forEach(i -> {
            for (int j = 0; j < colsB; j++) {
                final int column = j;
                result[i][column] = IntStream.range(0, colsA)
                        .mapToDouble(k -> A[i][k] * B[k][column])
                        .sum();
            }
        });

        return result;
    }
}
