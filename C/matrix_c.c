#include <stdlib.h>
#include <stdio.h>
#include <windows.h>
#include <psapi.h>
#include <sys/time.h>

#define n 512
double a[n][n];
double b[n][n];
double c[n][n];

ULONGLONG get_cpu_time_in_ms(FILETIME start_cpu_time) {
    FILETIME end_cpu_time;
    FILETIME dummy;
    GetProcessTimes(GetCurrentProcess(), &dummy, &dummy, &dummy, &end_cpu_time);

    ULONGLONG start_cpu = (((ULONGLONG)start_cpu_time.dwHighDateTime) << 32) | start_cpu_time.dwLowDateTime;
    ULONGLONG end_cpu = (((ULONGLONG)end_cpu_time.dwHighDateTime) << 32) | end_cpu_time.dwLowDateTime;

    return (end_cpu - start_cpu) / 10000; // in ms
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
    FILETIME start_cpu_time;
    FILETIME dummy;
    GetProcessTimes(GetCurrentProcess(), &dummy, &dummy, &dummy, &start_cpu_time);

    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            a[i][j] = (double)rand() / RAND_MAX;
            b[i][j] = (double)rand() / RAND_MAX;
            c[i][j] = 0;
        }
    }

    // Memory and CPU usage before multiplication
    SIZE_T memory_usage_before = get_memory_usage();
    ULONGLONG cpu_time_before = get_cpu_time_in_ms(start_cpu_time);

    gettimeofday(&start, NULL);

    // Matrix multiplication
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            for (int k = 0; k < n; ++k) {
                c[i][j] += a[i][k] * b[k][j];
            }
        }
    }

    gettimeofday(&stop, NULL);
    double diff = stop.tv_sec - start.tv_sec
                  + 1e-6 * (stop.tv_usec - start.tv_usec);
    printf("Time taken for multiplication: %0.6f seconds\n", diff);

    // Memory and CPU usage after multiplication
    SIZE_T memory_usage_after = get_memory_usage();
    ULONGLONG cpu_time_after = get_cpu_time_in_ms(start_cpu_time);

    ULONGLONG cpu_time_used = cpu_time_after - cpu_time_before;
    SIZE_T memory_used = memory_usage_after - memory_usage_before;

    printf("Memory used before: %zu KB\n", memory_usage_before);
    printf("Memory used after: %zu KB\n", memory_usage_after);
    printf("CPU time used: %llu ms\n", cpu_time_used);
    printf("Memory used: %zu KB\n", memory_used);

    return 0;
}
