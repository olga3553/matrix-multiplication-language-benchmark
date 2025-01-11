import numpy as np
import os
import psutil

def generate_matrix(rows, cols):
    return np.random.rand(rows, cols)

def log_performance(start_time, end_time):
    process = psutil.Process(os.getpid())
    memory_info = process.memory_info()
    cpu_percent = psutil.cpu_percent()

    print(f"Memory usage: {memory_info.rss / 1e6:.2f} MB")
    print(f"CPU usage: {cpu_percent:.2f}%")
