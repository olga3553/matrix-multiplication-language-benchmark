package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 4)
@Measurement(iterations = 4)
@Timeout(time = 300, timeUnit = TimeUnit.SECONDS)
public class MatrixMultiplicationBenchmarkingMc2depi {

    private SparseMatrix sparseMatrixA;
    private SparseMatrix sparseMatrixB;
    private SparseMatrixMultiplication sparseMultiplication;

    @Param({"../../test_matrix/mc2depi.mtx"})
    private String matrixFilePath;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        sparseMatrixA = loadMatrixFromFile(matrixFilePath);
        sparseMatrixB = sparseMatrixA;

        sparseMultiplication = new SparseMatrixMultiplication();
    }

    private SparseMatrix loadMatrixFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int rows = 0, cols = 0;
        SparseMatrix sparseMatrix = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("%")) {
                continue;
            }
            String[] parts = line.trim().split("\\s+");
            if (sparseMatrix == null) {
                rows = Integer.parseInt(parts[0]);
                cols = Integer.parseInt(parts[1]);
                sparseMatrix = new SparseMatrix(rows, cols);
            } else {
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                double value = Double.parseDouble(parts[2]);
                sparseMatrix.set(row, col, value);
            }
        }
        reader.close();
        return sparseMatrix;
    }

    @Benchmark
    public SparseMatrix benchmarkSparseMultiplication() {
        return sparseMultiplication.execute(sparseMatrixA, sparseMatrixB);
    }


}
