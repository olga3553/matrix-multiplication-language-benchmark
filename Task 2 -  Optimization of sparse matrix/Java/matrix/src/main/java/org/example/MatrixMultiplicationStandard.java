package org.example;

public class MatrixMultiplicationStandard {

	public double[][] execute(double[][] a, double[][] b) {
		int n = a.length;
		double[][] c = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double sum = 0.0;
				for (int k = 0; k < n; k++) {
					sum += a[i][k] * b[k][j];
				}
				c[i][j] = sum;
			}
		}
		return c;
	}
}
