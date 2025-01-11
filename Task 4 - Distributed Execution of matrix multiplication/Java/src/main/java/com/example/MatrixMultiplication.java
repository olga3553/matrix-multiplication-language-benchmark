package com.example;

import java.io.Serializable;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;

public class MatrixMultiplication {
    public static double[][] multiply(double[][] matrixA, double[][] matrixB, HazelcastInstance hazelcastInstance) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsB];
        IExecutorService executorService = hazelcastInstance.getExecutorService("default");

        List<Future<double[]>> futures = new ArrayList<>();
        for (int i = 0; i < rowsA; i++) {
            int row = i;
            futures.add(executorService.submit(new RowMultiplicationTask(row, matrixA, matrixB)));
        }

        for (int i = 0; i < rowsA; i++) {
            try {
                result[i] = futures.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class RowMultiplicationTask implements Callable<double[]>, Serializable {
        private final int row;
        private final double[][] matrixA;
        private final double[][] matrixB;

        public RowMultiplicationTask(int row, double[][] matrixA, double[][] matrixB) {
            this.row = row;
            this.matrixA = matrixA;
            this.matrixB = matrixB;
        }

        @Override
        public double[] call() {
            int colsB = matrixB[0].length;
            double[] resultRow = new double[colsB];
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    resultRow[j] += matrixA[row][k] * matrixB[k][j];
                }
            }
            return resultRow;
        }
    }
}
