package org.example;

public class SynchronizedMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        double[][] result = new double[rowsA][colsB];

        Thread[] threads = new Thread[rowsA];
        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            threads[row] = new Thread(() -> {
                for (int j = 0; j < colsB; j++) {
                    synchronized (result) {
                        result[row][j] = 0;
                        for (int k = 0; k < colsA; k++) {
                            result[row][j] += A[row][k] * B[k][j];
                        }
                    }
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

        return result;
    }
}
