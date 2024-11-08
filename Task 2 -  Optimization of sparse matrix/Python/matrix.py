import scipy.sparse as sp
import scipy.io as sio
import pytest


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

    result = benchmark(sparse_matrix_multiply, A, B)

    assert result is not None
    assert sp.issparse(result)
