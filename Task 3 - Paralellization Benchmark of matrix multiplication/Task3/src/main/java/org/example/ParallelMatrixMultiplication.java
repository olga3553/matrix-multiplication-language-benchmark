package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B, int numThreads) throws InterruptedException, ExecutionException {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        double[][] result = new double[rowsA][colsB];
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            futures.add(executor.submit(() -> {
                for (int j = 0; j < colsB; j++) {
                    result[row][j] = 0;
                    for (int k = 0; k < colsA; k++) {
                        result[row][j] += A[row][k] * B[k][j];
                    }
                }
            }));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();
        return result;
    }
}
