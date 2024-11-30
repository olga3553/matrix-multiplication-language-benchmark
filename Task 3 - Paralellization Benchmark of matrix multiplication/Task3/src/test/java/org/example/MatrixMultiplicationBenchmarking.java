package org.example;

import org.openjdk.jmh.annotations.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@Timeout(time = 180, timeUnit = TimeUnit.SECONDS)
public class MatrixMultiplicationBenchmarking {

    @Param({"2", "4", "8", "16", "32"})
    private int numThreads;


    @Param({"500", "1000", "1500", "2000"})
    private int SIZE;

    private double[][] matrixA;
    private double[][] matrixB;

    private ForkJoinMatrixMultiplication forkJoinMultiplication;

    private ForkJoinPool forkJoinPool;

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    private final List<Long> cpuTimes = new ArrayList<>();
    private final List<Long> memoryUsages = new ArrayList<>();

    @Setup(Level.Trial)
    public void setUp() {
        matrixA = generateRandomMatrix(SIZE);
        matrixB = generateRandomMatrix(SIZE);
        forkJoinPool = new ForkJoinPool();
        forkJoinMultiplication = new ForkJoinMatrixMultiplication(forkJoinPool);
        ParallelMatrixMultiplication parallelMultiplication = new ParallelMatrixMultiplication();
        StreamMatrixMultiplication streamMatrixMultiplication = new StreamMatrixMultiplication();
        AtomicMatrixMultiplication atomicMultiplication = new AtomicMatrixMultiplication();
        SynchronizedMatrixMultiplication synchronizedMultiplication = new SynchronizedMatrixMultiplication();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        forkJoinPool.shutdown();
        printAverageUsage();
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

    private void startMeasurement() {
        cpuTimes.add(threadMXBean.getCurrentThreadCpuTime());
        memoryUsages.add(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    private void endMeasurement() {
        int lastIndex = cpuTimes.size() - 1;
        long cpuTimeEnd = threadMXBean.getCurrentThreadCpuTime();
        long memoryEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long cpuTimeUsed = cpuTimeEnd - cpuTimes.get(lastIndex);
        long memoryUsed = memoryEnd - memoryUsages.get(lastIndex);

        cpuTimes.set(lastIndex, cpuTimeUsed);
        memoryUsages.set(lastIndex, Math.max(0, memoryUsed)); // Avoid negative memory usage
    }

    private void printAverageUsage() {
        long totalCpuTime = cpuTimes.stream().mapToLong(Long::longValue).sum();
        long totalMemoryUsage = memoryUsages.stream().mapToLong(Long::longValue).sum();

        double averageCpuTimeMs = totalCpuTime / (double) cpuTimes.size() / 1_000_000.0;
        double averageMemoryUsageKb = totalMemoryUsage / (double) memoryUsages.size() / 1024.0;

        System.out.printf("Average CPU Time: %.3f ms, Average Memory Used: %.3f KB%n",
                averageCpuTimeMs, averageMemoryUsageKb);
    }

    @Benchmark
    public double[][] benchmarkParallelMultiplication() throws ExecutionException, InterruptedException {
        startMeasurement();
        double[][] result = ParallelMatrixMultiplication.multiply(matrixA, matrixB, numThreads);
        endMeasurement();
        return result;
    }

    @Benchmark
    public double[][] benchmarkForkJoinMultiplication() {
        startMeasurement();
        double[][] result = forkJoinMultiplication.multiply(matrixA, matrixB);
        endMeasurement();
        return result;
    }

    @Benchmark
    public double[][] benchmarkStreamMultiplication() {
        startMeasurement();
        double[][] result = StreamMatrixMultiplication.multiply(matrixA, matrixB);
        endMeasurement();
        return result;
    }

    @Benchmark
    public double[][] benchmarkSynchronizedMultiplication() {
        startMeasurement();
        double[][] result = SynchronizedMatrixMultiplication.multiply(matrixA, matrixB);
        endMeasurement();
        return result;
    }

    @Benchmark
    public double[][] benchmarkAtomicMultiplication() {
        startMeasurement();
        double[][] result = AtomicMatrixMultiplication.multiply(matrixA, matrixB);
        endMeasurement();
        return result;
    }

    @Benchmark
    public double[][] benchmarkVectorizedMultiplication() throws ExecutionException, InterruptedException {
        startMeasurement();
        double[][] result = VectorizedMatrixMultiplication.multiply(matrixA, matrixB, numThreads);
        endMeasurement();
        return result;
    }

    @Benchmark
    public double[][] benchmarkFixedThreadsMultiplication() {
        startMeasurement();
        double[][] result = MatrixMultiplicationFixedThreads.multiply(matrixA, matrixB, Runtime.getRuntime().availableProcessors());
        endMeasurement();
        return result;
    }
}
