#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>

#define MAX_SIZE 1024

#define CONTROLLER_PIPE_REQ "../temp/controller_pipe_req"
#define CONTROLLER_PIPE_RES "../temp/controller_pipe_res"

int write_request(char *req) {
	int pipefd = open(CONTROLLER_PIPE_REQ, O_WRONLY);
	int count = write(pipefd, req, strlen(req)+1);
	close(pipefd);

	return count;
}

int read_response(char *res) {
	int pipefd = open(CONTROLLER_PIPE_RES, O_RDONLY);
	int count = read(pipefd, res, MAX_SIZE);
	close(pipefd);

	return count;
}

int start_controller(int secure_bit) {
	mkfifo(CONTROLLER_PIPE_REQ, 0666);
	mkfifo(CONTROLLER_PIPE_RES, 0666);
    int pid = fork();
    if(pid != 0) {
    	char res[MAX_SIZE];
    	read_response(res);
    	return strtol(res, NULL, 10);
    } else {
    	char secure_bit_arg[2];
    	sprintf(secure_bit_arg, "%d", secure_bit);
        char * const args[]={(char *)"../bin/sdn_controller", secure_bit_arg, (char *)NULL};
        execvp(args[0], args);
        return 0;
    }
}

int add_new_flow(int path_len, int path[], unsigned long long *time_taken, int *data_sent) {
    char req[MAX_SIZE], time_and_data[MAX_SIZE], *data_ptr;
    sprintf(req, "1 %d ", path_len);
    for(int i=0;i<path_len;i++) {
        char temp_path[MAX_SIZE];
        sprintf(temp_path, "%d ", path[i]);
        strcat(req, temp_path);
    }

    write_request(req);
    read_response(time_and_data);

    *time_taken = strtoll(time_and_data, &data_ptr, 10);
    *data_sent = strtol(data_ptr+1, NULL, 10);

    return 0;
}

int dump_state() {
    char req[MAX_SIZE];
    strcpy(req, "2");

    return write_request(req);
}

int exit_controller() {
    char req[MAX_SIZE];
    strcpy(req, "3");

    return write_request(req);
}
