package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VectorizedMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B, int numThreads) throws InterruptedException, ExecutionException {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        if (colsA != B.length) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        double[][] result = new double[rowsA][colsB];
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            futures.add(executor.submit(() -> {
                for (int j = 0; j < colsB; j++) {
                    result[row][j] = vectorizedDotProduct(A[row], getColumn(B, j));
                }
            }));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();
        return result;
    }

    private static double vectorizedDotProduct(double[] row, double[] column) {
        double sum = 0.0;
        for (int i = 0; i < row.length; i++) {
            sum += row[i] * column[i];
        }
        return sum;
    }

    private static double[] getColumn(double[][] matrix, int colIndex) {
        double[] column = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colIndex];
        }
        return column;
    }
}
