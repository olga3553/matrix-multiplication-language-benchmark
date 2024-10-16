package org.example;

import org.openjdk.jmh.annotations.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MatrixMultiplicationBenchmarking {

	@State(Scope.Thread)
	public static class Operands {
		private final int n = 64;
		private final double[][] a = new double[n][n];
		private final double[][] b = new double[n][n];

		@Setup
		public void setup() {
			Random random = new Random();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					a[i][j] = random.nextDouble();
					b[i][j] = random.nextDouble();
				}
			}
		}
	}

	@State(Scope.Thread)
	public static class ResourceUsage {
		long totalMemoryUsed = 0;
		long totalCpuTimeUsed = 0;
		int iterations = 0;

		// Method to accumulate usage
		public void accumulate(long memoryUsed, long cpuTimeUsed) {
			totalMemoryUsed += memoryUsed;
			totalCpuTimeUsed += cpuTimeUsed;
			iterations++;
		}

		@TearDown
		public void printAverages() {
			if (iterations > 0) {
				double averageMemoryUsage = (double) totalMemoryUsed / iterations;
				double averageCpuTimeUsed = (double) totalCpuTimeUsed / iterations;

				System.out.println("Average Memory Usage: " + averageMemoryUsage + " bytes");
				System.out.println("Average CPU Time Used: " + averageCpuTimeUsed + " ms");
			}
		}
	}

	@Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.MILLISECONDS)
	@Benchmark
	public void multiplication(Operands operands, ResourceUsage resourceUsage) {
		// Capture Memory usage and CPU before execution
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		long beforeCpuTime = threadMXBean.getCurrentThreadCpuTime();

		// Execute the multiplication
		new MatrixMultiplication().execute(operands.a, operands.b);

		// Memory usage and CPU after execution
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long afterCpuTime = threadMXBean.getCurrentThreadCpuTime();

		// Calculate memory and CPU usage
		long memoryUsed = afterUsedMem - beforeUsedMem;
		long cpuTimeUsed = (afterCpuTime - beforeCpuTime) / 1_000_000; // Convert to milliseconds

		// Accumulate totals
		resourceUsage.accumulate(memoryUsed, cpuTimeUsed);
	}
}
