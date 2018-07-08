#include <stdio.h>
#include <time.h>
#include <stdint.h>
#include <windows.h>
#include <cuda.h>
#include <cuda_runtime_api.h>
#include "device_launch_parameters.h"

int NUMBER_OF_SM, NUMBER_OF_THREADS_PER_SM;

LARGE_INTEGER start, end;
LARGE_INTEGER freq;

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

void initialize_gpu_parameters(cudaDeviceProp devProp)
{
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

int main()
{
	cudaDeviceProp devProp;
	cudaGetDeviceProperties(&devProp, 0);
	initialize_gpu_parameters(devProp);
	initialize_timing_and_randomness();

	printf("Enter matrix size:");
	int mat_sz;
	scanf("%d", &mat_sz);
	getchar();

	printf("Would you like to see the random matrices and the resultant matrix?(YES - 1, NO - 0):");
	int is_debug_mode;
	scanf("%d", &is_debug_mode);
	getchar();

	const int MATRIX_SIZE = mat_sz;
	int *host_mat1, *host_mat2, *host_mat3, *device_mat1, *device_mat2, *device_mat3;
	int no_blks, no_threads;
	int i = 0, j = 0;

	host_mat1 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			host_mat1[i * MATRIX_SIZE + j] = rand() % 10;
		}
	}

	host_mat2 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			host_mat2[i * MATRIX_SIZE + j] = rand() % 10;
		}
	}

	host_mat3 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int *));

	if (is_debug_mode)
	{
		printf("Matrix 1:\n");
		display_matrix(host_mat1, MATRIX_SIZE);
		printf("Matrix 2:\n");
		display_matrix(host_mat2, MATRIX_SIZE);
	}

	QueryPerformanceCounter(&start);

	cudaMalloc((void **)&device_mat1, MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	cudaMalloc((void **)&device_mat2, MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	cudaMalloc((void **)&device_mat3, MATRIX_SIZE * MATRIX_SIZE * sizeof(int));

	cudaMemcpy(device_mat1, host_mat1, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyHostToDevice);
	cudaMemcpy(device_mat2, host_mat2, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyHostToDevice);

	const int total_elements = MATRIX_SIZE * MATRIX_SIZE;
	if (total_elements <= NUMBER_OF_THREADS_PER_SM)
	{
		no_blks = 1;
		no_threads = total_elements;
	}
	else
	{
		no_threads = NUMBER_OF_THREADS_PER_SM;
		no_blks = total_elements / NUMBER_OF_THREADS_PER_SM + 1;
	}

	matmul << <dim3(no_blks), dim3(no_threads) >> >(device_mat1, device_mat2, device_mat3, MATRIX_SIZE, NUMBER_OF_THREADS_PER_SM);

	cudaMemcpy(host_mat3, device_mat3, MATRIX_SIZE * MATRIX_SIZE * sizeof(int), cudaMemcpyDeviceToHost);

	QueryPerformanceCounter(&end);

	if (is_debug_mode)
	{
		printf("Result:\n");
		display_matrix(host_mat3, MATRIX_SIZE);
	}

	printf("Time taken:\n");
	long long time_taken = (end.QuadPart - start.QuadPart) / (freq.QuadPart / 1000);
	printf("%lld ms", time_taken);

	free(host_mat1);
	free(host_mat2);
	free(host_mat3);

	cudaFree(device_mat1);
	cudaFree(device_mat2);
	cudaFree(device_mat3);

	getchar();

	return 0;
}