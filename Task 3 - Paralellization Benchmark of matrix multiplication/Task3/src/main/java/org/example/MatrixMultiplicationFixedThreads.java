package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplicationFixedThreads {

    public static double[][] multiply(double[][] A, double[][] B, int numThreads) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        if (colsA != B.length) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        double[][] result = new double[rowsA][colsB];
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int row = 0; row < rowsA; row++) {
            final int currentRow = row;
            executorService.submit(() -> multiplyRow(currentRow, A, B, result));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void multiplyRow(int row, double[][] A, double[][] B, double[][] result) {
        int colsB = B[0].length;
        int colsA = A[0].length;

        for (int j = 0; j < colsB; j++) {
            for (int k = 0; k < colsA; k++) {
                result[row][j] += A[row][k] * B[k][j];
            }
        }
    }
}
