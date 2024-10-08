import numpy as np
import pytest
import psutil
import time

def matrix_multiply(a, b):
    n = len(a)
    c = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            for k in range(n):
                c[i][j] += a[i][k] * b[k][j]
    return c

@pytest.fixture
def setup_matrices():
    size = 32
    A = np.random.rand(size, size)
    B = np.random.rand(size, size)
    return A, B

@pytest.mark.benchmark(min_rounds=1)
def test_matrix_multiply(benchmark, setup_matrices):
    A, B = setup_matrices

    # Memory and CPU before
    process = psutil.Process()
    cpu_start = time.process_time()
    mem_before = process.memory_info().rss / (1024 ** 2)  # in MB

    result = benchmark(matrix_multiply, A, B)

    # Memory and CPU after
    cpu_end = time.process_time()
    mem_after = process.memory_info().rss / (1024 ** 2)  # in MB

    cpu_time_ms = (cpu_end - cpu_start) * 1000  # in ms
    mem_usage = max(mem_after - mem_before, 0)

    print()
    print(f"Czas procesora: {cpu_time_ms:.2f} ms")
    print(f"Zużycie pamięci: {mem_usage:.2f} MB")

    assert result is not None
