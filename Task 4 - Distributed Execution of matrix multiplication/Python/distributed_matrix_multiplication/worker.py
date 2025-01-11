from mpi4py import MPI
import numpy as np

if __name__ == "__main__":
    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()

    while True:
        data = comm.recv(source=0, tag=11)
        if data == "STOP":
            break
        matrix_a, matrix_b = data
        result = np.dot(matrix_a, matrix_b)
        comm.send(result, dest=0, tag=22)
