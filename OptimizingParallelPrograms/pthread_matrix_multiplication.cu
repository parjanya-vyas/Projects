#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdint.h>
#include <windows.h>
#include <pthread.h>

int MATRIX_SIZE;
int *mat1, *mat2, *mat3;

LARGE_INTEGER start, end;
LARGE_INTEGER freq;

void* pthread_mat_mul(void *arg)
{
	int i, j, k;
	i = *(int *)arg;
	j = *((int *)(arg) + 1);
	for (k = 0; k < MATRIX_SIZE; k++)
	{
		mat3[i * MATRIX_SIZE + j] += mat1[i * MATRIX_SIZE + k] * mat2[k * MATRIX_SIZE + j];
	}

	return (void *)0;
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
	printf("Enter matrix size:");
	int mat_sz;
	scanf("%d", &mat_sz);
	getchar();

	printf("Would you like to see the random matrices and the resultant matrix?(YES - 1, NO - 0):");
	int is_debug_mode;
	scanf("%d", &is_debug_mode);
	getchar();

	MATRIX_SIZE = mat_sz;
	const int arg_sz = MATRIX_SIZE * MATRIX_SIZE;
	int i = 0, j = 0, k = 0;

	initialize_timing_and_randomness();

	mat1 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			mat1[i * MATRIX_SIZE + j] = rand() % 10;
		}
	}

	mat2 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			mat2[i * MATRIX_SIZE + j] = rand() % 10;
		}
	}

	mat3 = (int *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(int *));
	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++)
		{
			mat3[i * MATRIX_SIZE + j] = 0;
		}
	}

	QueryPerformanceCounter(&start);

	pthread_t *threads = (pthread_t *)malloc(MATRIX_SIZE * MATRIX_SIZE * sizeof(pthread_t));
	int *thread_args = (int *)malloc(arg_sz * 2 * sizeof(int));

	for (i = 0; i < MATRIX_SIZE; i++)
	{
		for (j = 0; j < MATRIX_SIZE; j++, k+=2)
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

	QueryPerformanceCounter(&end);

	if (is_debug_mode)
	{
		printf("Matrix 1:\n");
		display_matrix(mat1, MATRIX_SIZE);
		printf("Matrix 2:\n");
		display_matrix(mat2, MATRIX_SIZE);
		printf("Result:\n");
		display_matrix(mat3, MATRIX_SIZE);
	}

	printf("Time taken:\n");
	long long time_taken = (end.QuadPart - start.QuadPart) / (freq.QuadPart / 1000);
	printf("%lld ms", time_taken);

	getchar();
	return 0;
}