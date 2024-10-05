#include <stdlib.h>
#include <stdio.h>
#include <windows.h>
#include <psapi.h>
#include <sys/time.h>

#define n 512
double a[n][n];
double b[n][n];
double c[n][n];

// Function to measure CPU
double get_cpu_usage(SYSTEMTIME start_sys_time, FILETIME start_cpu_time) {
    SYSTEMTIME end_sys_time;
    FILETIME end_cpu_time;

    GetSystemTime(&end_sys_time);
    FILETIME dummy;
    GetProcessTimes(GetCurrentProcess(), &dummy, &dummy, &dummy, &end_cpu_time);

    // Time converstion
    ULONGLONG start_cpu = (((ULONGLONG)start_cpu_time.dwHighDateTime) << 32) | start_cpu_time.dwLowDateTime;
    ULONGLONG end_cpu = (((ULONGLONG)end_cpu_time.dwHighDateTime) << 32) | end_cpu_time.dwLowDateTime;

    ULONGLONG total_time_ms = (end_sys_time.wSecond * 1000 + end_sys_time.wMilliseconds) -
                              (start_sys_time.wSecond * 1000 + start_sys_time.wMilliseconds);

    ULONGLONG cpu_time_ms = (end_cpu - start_cpu) / 10000;

    return (double)cpu_time_ms / total_time_ms * 100;
}

// Memory usage
SIZE_T get_memory_usage() {
    PROCESS_MEMORY_COUNTERS_EX memInfo;
    GetProcessMemoryInfo(GetCurrentProcess(), (PROCESS_MEMORY_COUNTERS*)&memInfo, sizeof(memInfo));
    return memInfo.WorkingSetSize / 1024; // in KB
}

struct timeval start, stop;
int main() {
    // Start time
    SYSTEMTIME start_sys_time;
    FILETIME start_cpu_time;
    GetSystemTime(&start_sys_time);
    FILETIME dummy;
    GetProcessTimes(GetCurrentProcess(), &dummy, &dummy, &dummy, &start_cpu_time);

    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            a[i][j] = (double)rand() / RAND_MAX;
            b[i][j] = (double)rand() / RAND_MAX;
            c[i][j] = 0;
        }
    }
    gettimeofday(&start,NULL);
    // Matrix multiplication
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            for (int k = 0; k < n; ++k) {
                c[i][j] += a[i][k] * b[k][j];
            }
        }
    }

    gettimeofday(&stop,NULL);
    double diff = stop.tv_sec - start.tv_sec
                  + 1e-6*(stop.tv_usec - start.tv_usec);
    printf("%0.6f\n",diff);

    double cpu_usage = get_cpu_usage(start_sys_time, start_cpu_time);
    SIZE_T memory_usage = get_memory_usage();

    printf("CPU usage: %.2f%%\n", cpu_usage);
    printf("Memory usage: %zu KB\n", memory_usage);

    return 0;
}
