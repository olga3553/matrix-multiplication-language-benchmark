from mpi4py import MPI
import numpy as np
import time
from utils import generate_matrix, log_performance

def distribute_work(matrix_a, matrix_b, size):
    rows_a = matrix_a.shape[0]
    chunk_size = rows_a // (size - 1)

    for i in range(1, size):
        start_row = (i - 1) * chunk_size
        end_row = start_row + chunk_size if i < size - 1 else rows_a
        comm.send((matrix_a[start_row:end_row], matrix_b), dest=i, tag=11)

def gather_results(size, rows_a):
    result = []
    for i in range(1, size):
        partial_result = comm.recv(source=i, tag=22)
        result.append(partial_result)
    return np.vstack(result)

if __name__ == "__main__":
    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()
    size = comm.Get_size()

    if rank == 0:
        n = 10000
        matrix_a = generate_matrix(n, n)
        matrix_b = generate_matrix(n, n)

        print(f"Starting distributed matrix multiplication for {n}x{n} matrices.")
        start_time = time.time()
        distribute_work(matrix_a, matrix_b, size)
        result = gather_results(size, n)
        end_time = time.time()

        execution_time_ms = (end_time - start_time) * 1000

        print(f"Multiplication completed in: {execution_time_ms:.2f} ms")
        log_performance(start_time, end_time)

    else:
        data = comm.recv(source=0, tag=11)
        matrix_a, matrix_b = data
        result = np.dot(matrix_a, matrix_b)
        comm.send(result, dest=0, tag=22)
