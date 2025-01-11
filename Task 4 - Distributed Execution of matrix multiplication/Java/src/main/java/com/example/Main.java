package com.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        int size = 50;

        double[][] matrixA = MatrixGenerator.generate(size, size);
        double[][] matrixB = MatrixGenerator.generate(size, size);

        long startTime = System.currentTimeMillis();
        double[][] result = MatrixMultiplication.multiply(matrixA, matrixB, hazelcastInstance);
        long endTime = System.currentTimeMillis();


        System.out.println("Matrix multiplication completed in " + (endTime - startTime) + " ms");
        System.out.println("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) + " MB");


        hazelcastInstance.shutdown();
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}