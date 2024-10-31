import numpy as np
import scipy.sparse as sp
import scipy.io as sio
import pytest
import psutil
import time

def sparse_matrix_multiply(a, b):
    return a.dot(b)

@pytest.fixture
def setup_matrices():
    # Load matrices from .mtx files
    A = sio.mmread("../test_matrix/mc2depi.mtx").tocsr()
    B = sio.mmread("../test_matrix/mc2depi.mtx").tocsr()
    return A, B

@pytest.mark.benchmark(min_rounds=5)
def test_sparse_matrix_multiply(benchmark, setup_matrices):
    A, B = setup_matrices

    # Memory and CPU usage before
    process = psutil.Process()
    cpu_start = time.process_time()
    mem_before = process.memory_info().rss

    result = benchmark(sparse_matrix_multiply, A, B)

    # Memory and CPU usage after
    cpu_end = time.process_time()
    mem_after = process.memory_info().rss

    cpu_time_ms = (cpu_end - cpu_start) * 1000
    mem_usage = max(mem_after - mem_before, 0)

    print()
    print(f"CPU time: {cpu_time_ms:.2f} ms")
    print(f"Memory usage: {mem_usage} bytes")

    assert result is not None
    assert sp.issparse(result)
