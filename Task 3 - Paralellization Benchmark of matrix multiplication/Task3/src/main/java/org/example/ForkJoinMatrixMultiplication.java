package org.example;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinMatrixMultiplication {

    private final ForkJoinPool pool;

    public ForkJoinMatrixMultiplication(ForkJoinPool pool) {
        this.pool = pool;
    }

    public double[][] multiply(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsB = B[0].length;
        double[][] result = new double[rowsA][colsB];

        pool.invoke(new MatrixMultiplyTask(A, B, result, 0, rowsA));
        return result;
    }

    private static class MatrixMultiplyTask extends RecursiveTask<Void> {
        private static final int THRESHOLD = 64;
        private final double[][] A;
        private final double[][] B;
        private final double[][] result;
        private final int startRow;
        private final int endRow;

        public MatrixMultiplyTask(double[][] A, double[][] B, double[][] result, int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected Void compute() {
            if (endRow - startRow <= THRESHOLD) {
                multiplyDirectly();
            } else {
                int mid = (startRow + endRow) / 2;
                MatrixMultiplyTask topHalf = new MatrixMultiplyTask(A, B, result, startRow, mid);
                MatrixMultiplyTask bottomHalf = new MatrixMultiplyTask(A, B, result, mid, endRow);

                invokeAll(topHalf, bottomHalf);
            }
            return null;
        }

        private void multiplyDirectly() {
            int colsA = A[0].length;
            int colsB = B[0].length;

            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < colsB; j++) {
                    result[i][j] = 0;
                    for (int k = 0; k < colsA; k++) {
                        result[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        }
    }
}
