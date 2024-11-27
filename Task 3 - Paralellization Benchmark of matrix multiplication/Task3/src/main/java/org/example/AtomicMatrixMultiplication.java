package org.example;

import com.google.common.util.concurrent.AtomicDoubleArray;

public class AtomicMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        double[][] result = new double[rowsA][colsB];
        AtomicDoubleArray[] atomicResult = new AtomicDoubleArray[rowsA];
        for (int i = 0; i < rowsA; i++) {
            atomicResult[i] = new AtomicDoubleArray(colsB);
        }

        Thread[] threads = new Thread[rowsA];
        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            threads[row] = new Thread(() -> {
                for (int j = 0; j < colsB; j++) {
                    double value = 0;
                    for (int k = 0; k < colsA; k++) {
                        value += A[row][k] * B[k][j];
                    }
                    atomicResult[row].set(j, value);
                }
            });
            threads[row].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                result[i][j] = atomicResult[i].get(j);
            }
        }

        return result;
    }
}
