#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdint.h>
#include <windows.h>
#include <pthread.h>
#include <cuda.h>
#include <cuda_runtime_api.h>
#include "device_launch_parameters.h"

int MATRIX_SIZE;
int NUMBER_OF_SM, NUMBER_OF_THREADS_PER_SM;
int NUMBER_OF_CPU_BLOCKS;
int no_blks, no_threads;
int is_debug_mode, is_gpu_used;
int *mat1, *mat2, *pthread_mat3, *cuda_mat3, *final_result_mat3;
int *device_mat1, *device_mat2, *device_mat3;

LARGE_INTEGER start, end;
LARGE_INTEGER freq;

void* pthread_mat_mul(void *arg)
{
	int i, j, k;
	i = *(int *)arg;
	j = *((int *)(arg)+1);
	for (k = 0; k < MATRIX_SIZE; k++)
	{
		pthread_mat3[i * MATRIX_SIZE + j] += mat1[i * MATRIX_SIZE + k] * mat2[k * MATRIX_SIZE + j];
	}

	return (void *)0;
}

void calculate_results_pthreads()
{
	const int arg_sz = MATRIX_SIZE * MATRIX_SIZE;
	int i = 0, j = 0, k = 0;

	pthread_t *threads = (pthread_t *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(pthread_t));
	int *thread_args = (int *)malloc(arg_sz * 2 * sizeof(int));

	if (!is_gpu_used)
	{
		for (i = 0; i < MATRIX_SIZE; i++)
		{
			for (j = 0; j < MATRIX_SIZE; j++, k += 2)
			{
				thread_args[k] = i;
				thread_args[k + 1] = j;
				pthread_create(&threads[i * MATRIX_SIZE + j], NULL, pthread_mat_mul, &thread_args[k]);
			}
		}

		for (i = 0; i < MATRIX_SIZE; i++)
		{
			for (j = 0; j < MATRIX_SIZE; j++)
			{
				pthread_join(threads[i * MATRIX_SIZE + j], NULL);
			}
		}
	}
	else
	{
		for (i = 0; i < MATRIX_SIZE; i++)
		{
			for (j = 0; j < MATRIX_SIZE; j++, k += 2)
			{
				int ele = i * MATRIX_SIZE + j;
				int blk = ele / no_threads;
				if (blk >= no_blks)
				{
					thread_args[k] = i;
					thread_args[k + 1] = j;
					pthread_create(&threads[i * MATRIX_SIZE + j], NULL, pthread_mat_mul, &thread_args[k]);
				}
			}
		}

		for (i = 0; i < MATRIX_SIZE; i++)
		{
			for (j = 0; j < MATRIX_SIZE; j++)
			{
				int ele = i * MATRIX_SIZE + j;
				int blk = ele / no_threads;
				if (blk >= no_blks)
				{
					pthread_join(threads[i * MATRIX_SIZE + j], NULL);
				}
			}
		}
	}
}

__global__ void matmul(int *device_mat1, int *device_mat2, int *device_mat3, int sz, int max_thread)
{
	int row, col, ele, i;
	int temp = 0;
	ele = blockIdx.x * max_thread + threadIdx.x + 1;
	row = (ele - 1) / sz;
	if (row >= sz)
		return;
	col = (ele - 1) % sz;
	for (i = 0; i < sz; i++)
	{
		temp += device_mat1[row * sz + i] * device_mat2[i * sz + col];
	}
	device_mat3[row * sz + col] = temp;
}

void calculate_results_cuda()
{
	int i = 0, j = 0;

	cudaMalloc((void **)&device_mat1, MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	cudaMalloc((void **)&device_mat2, MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	cudaMalloc((void **)&device_mat3, MATRIX_SIZE * MATRIX_SIZE * sizeof(int));

	cudaMemcpy(device_mat1, mat1, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyHostToDevice);
	cudaMemcpy(device_mat2, mat2, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyHostToDevice);
	cudaMemcpy(device_mat3, cuda_mat3, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyHostToDevice);

	const int total_elements = MATRIX_SIZE * MATRIX_SIZE;
	no_threads = NUMBER_OF_THREADS_PER_SM;
	no_blks = (total_elements / NUMBER_OF_THREADS_PER_SM + 1) - NUMBER_OF_CPU_BLOCKS;

	matmul << <dim3(no_blks), dim3(no_threads) >> >(device_mat1, device_mat2, device_mat3, MATRIX_SIZE, NUMBER_OF_THREADS_PER_SM);

	cudaMemcpy(cuda_mat3, device_mat3, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyDeviceToHost);
}

void calculate_results()
{
	if (!is_gpu_used)
	{
		calculate_results_pthreads();
		QueryPerformanceCounter(&end);
	}
	else
	{
		calculate_results_cuda();
		calculate_results_pthreads();
		QueryPerformanceCounter(&end);
		for (int i = 0; i < MATRIX_SIZE; i++)
		{
			for (int j = 0; j < MATRIX_SIZE; j++)
			{
				if (cuda_mat3[i * MATRIX_SIZE + j] != 0)
					final_result_mat3[i * MATRIX_SIZE + j] = cuda_mat3[i * MATRIX_SIZE + j];
				else
					final_result_mat3[i * MATRIX_SIZE + j] = pthread_mat3[i * MATRIX_SIZE + j];
			}
		}
	}
}

inline int _ConvertSMVer2Cores(int major, int minor)
{
	// Defines for GPU Architecture types (using the SM version to determine the # of cores per SM
	typedef struct
	{
		int SM; // 0xMm (hexidecimal notation), M = SM Major version, and m = SM minor version
		int Cores;
	} sSMtoCores;

	sSMtoCores nGpuArchCoresPerSM[] =
	{
		{ 0x20, 32 }, // Fermi Generation (SM 2.0) GF100 class
		{ 0x21, 48 }, // Fermi Generation (SM 2.1) GF10x class
		{ 0x30, 192 }, // Kepler Generation (SM 3.0) GK10x class
		{ 0x32, 192 }, // Kepler Generation (SM 3.2) GK10x class
		{ 0x35, 192 }, // Kepler Generation (SM 3.5) GK11x class
		{ 0x37, 192 }, // Kepler Generation (SM 3.7) GK21x class
		{ 0x50, 128 }, // Maxwell Generation (SM 5.0) GM10x class
		{ 0x52, 128 }, // Maxwell Generation (SM 5.2) GM20x class
		{ 0x53, 128 }, // Maxwell Generation (SM 5.3) GM20x class
		{ 0x60, 64 }, // Pascal Generation (SM 6.0) GP100 class
		{ 0x61, 128 }, // Pascal Generation (SM 6.1) GP10x class
		{ 0x62, 128 }, // Pascal Generation (SM 6.2) GP10x class
		{ -1, -1 }
	};

	int index = 0;

	while (nGpuArchCoresPerSM[index].SM != -1)
	{
		if (nGpuArchCoresPerSM[index].SM == ((major << 4) + minor))
		{
			return nGpuArchCoresPerSM[index].Cores;
		}

		index++;
	}

	// If we don't find the values, we default use the previous one to run properly
	printf("MapSMtoCores for SM %d.%d is undefined.  Default to use %d Cores/SM\n", major, minor, nGpuArchCoresPerSM[index - 1].Cores);
	return nGpuArchCoresPerSM[index - 1].Cores;
}

void initialize_gpu_parameters()
{
	cudaDeviceProp devProp;
	cudaGetDeviceProperties(&devProp, 0);
	NUMBER_OF_SM = devProp.multiProcessorCount;
	NUMBER_OF_THREADS_PER_SM = _ConvertSMVer2Cores(devProp.major, devProp.minor);
}

void initialize_timing_and_randomness()
{
	srand(time(NULL));
	if (QueryPerformanceFrequency(&freq) == 0)
	{
		printf("Your machine not support high resolution performance counter\n");
		return;
	}
}

void check_if_gpu_is_needed()
{
	if (NUMBER_OF_CPU_BLOCKS * NUMBER_OF_THREADS_PER_SM >= MATRIX_SIZE * MATRIX_SIZE)
	{
		is_gpu_used = 0;
		printf("GPU not needed!\n");
	}
	else
	{
		is_gpu_used = 1;
		printf("Using GPU!\n");
	}
}

void display_matrix(int *mat, int sz)
{
	int i, j;
	for (i = 0; i < sz; i++)
	{
		for (j = 0; j < sz; j++)
		{
			printf("%d\t", mat[i * sz + j]);
		}
		printf("\n");
	}
}

void get_user_input()
{
	printf("Enter matrix size:");
	int mat_sz;
	scanf("%d", &MATRIX_SIZE);
	getchar();

	printf("Enter CPU/GPU factor:");
	scanf("%d", &NUMBER_OF_CPU_BLOCKS);
	getchar();

	printf("Would you like to see the random matrices and the resultant matrix?(YES - 1, NO - 0):");
	scanf("%d", &is_debug_mode);
	getchar();
}

void print_results()
{
	if (is_debug_mode)
	{
		printf("Result:\n");
		if (is_gpu_used)
			display_matrix(final_result_mat3, MATRIX_SIZE);
		else
			display_matrix(pthread_mat3, MATRIX_SIZE);
	}

	printf("Time taken:\n");
	long long time_taken = (end.QuadPart - start.QuadPart) / (freq.QuadPart / 1000);
	printf("%lld ms", time_taken);
}

void initialize_random_matrices()
{
	int i = 0, j = 0;

	mat1 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			mat1[i * MATRIX_SIZE + j] = (rand() % 99) + 1;
		}
	}

	mat2 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			mat2[i * MATRIX_SIZE + j] = (rand() % 99) + 1;
		}
	}

	if (is_debug_mode)
	{
		printf("Matrix 1:\n");
		display_matrix(mat1, MATRIX_SIZE);
		printf("Matrix 2:\n");
		display_matrix(mat2, MATRIX_SIZE);
	}

	pthread_mat3 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			pthread_mat3[i * MATRIX_SIZE + j] = 0;
		}
	}

	cuda_mat3 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			cuda_mat3[i * MATRIX_SIZE + j] = 0;
		}
	}

	final_result_mat3 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
}

void free_matrices()
{
	free(mat1);
	free(mat2);
	free(cuda_mat3);
	free(pthread_mat3);
	free(final_result_mat3);

	if (is_gpu_used)
	{
		cudaFree(device_mat1);
		cudaFree(device_mat2);
		cudaFree(device_mat3);
	}
}

int main()
{
	initialize_gpu_parameters();
	get_user_input();
	check_if_gpu_is_needed();
	initialize_timing_and_randomness();
	initialize_random_matrices();

	QueryPerformanceCounter(&start);
	calculate_results();
//	QueryPerformanceCounter(&end);

	print_results();
	free_matrices();

	getchar();
	return 0;
}