package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 0)
@Measurement(iterations = 4)
public class MatrixMultiplicationBenchmarking {

	private static final int SIZE = 2048;
	private double[][] matrixA;
	private double[][] matrixB;
	private SparseMatrix sparseMatrixA;
	private SparseMatrix sparseMatrixB;
	private MatrixMultiplicationBlocked blockedMultiplication;
	private MatrixMultiplicationStandard standardMultiplication;
	private SparseMatrixMultiplication sparseMultiplication;
	private MatrixMultiplicationParallelStreams parallelStreamsMultiplication;

	@Param({"0.0", "0.9"})
	private double sparsity;

	@Setup(Level.Trial)
	public void setUp() {
		matrixA = generateRandomMatrix(SIZE);
		matrixB = generateRandomMatrix(SIZE);
		blockedMultiplication = new MatrixMultiplicationBlocked();
		standardMultiplication = new MatrixMultiplicationStandard();
		sparseMatrixA = generateSparseMatrix(SIZE, sparsity);
		sparseMatrixB = generateSparseMatrix(SIZE, sparsity);
		sparseMultiplication = new SparseMatrixMultiplication();
		parallelStreamsMultiplication = new MatrixMultiplicationParallelStreams();
	}

	private double[][] generateRandomMatrix(int size) {
		Random random = new Random();
		double[][] matrix = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = random.nextDouble();
			}
		}
		return matrix;
	}

	private SparseMatrix generateSparseMatrix(int size, double sparsity) {
		Random random = new Random();
		SparseMatrix sparseMatrix = new SparseMatrix(size, size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (random.nextDouble() > sparsity) {
					sparseMatrix.set(i, j, random.nextDouble());
				}
			}
		}
		return sparseMatrix;
	}

	@Benchmark
	public double[][] benchmarkBlockedMultiplication() {
		return blockedMultiplication.execute(matrixA, matrixB);
	}

	@Benchmark
	public double[][] benchmarkStandardMultiplication() {
		return standardMultiplication.execute(matrixA, matrixB);
	}

	@Benchmark
	public SparseMatrix benchmarkSparseMultiplication() {
		return sparseMultiplication.execute(sparseMatrixA, sparseMatrixB);
	}

	@Benchmark
	public double[][] benchmarkParallelStreamsMultiplication() {
		return parallelStreamsMultiplication.execute(matrixA, matrixB);
	}
}
