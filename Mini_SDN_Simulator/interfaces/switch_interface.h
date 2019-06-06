#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>

#define MAX_SIZE 1024

int write_request_switch(char *req, char *switch_pipe_req) {
	int pipefd = open(switch_pipe_req, O_WRONLY);
	int count = write(pipefd, req, strlen(req)+1);
	close(pipefd);

	return count;
}

int read_response_switch(char *res, char *switch_pipe_res) {
	int pipefd = open(switch_pipe_res, O_RDONLY);
	int count = read(pipefd, res, MAX_SIZE);
	close(pipefd);

	return count;
}

int start_switch(int secure_bit, int *switch_port) {
    int pid = fork();
    if(pid != 0) {
    	char res[MAX_SIZE], pipename_req[MAX_SIZE], pipename_res[MAX_SIZE];
		sprintf(pipename_req, "../temp/switch_pipe_%d_req", pid);
		sprintf(pipename_res, "../temp/switch_pipe_%d_res", pid);
		mkfifo(pipename_req, 0666);
		mkfifo(pipename_res, 0666);
    	read_response_switch(res, pipename_res);
    	*switch_port = strtol(res, NULL, 10);
        return pid;
    } else {
    	char secure_bit_arg[2];
		sprintf(secure_bit_arg, "%d", secure_bit);
        char * const args[]={(char *)"../bin/sdn_switch", secure_bit_arg, (char *)NULL};
        execvp(args[0], args);
        return 0;
    }
}

int connect_switch_to_controller(int controller_port, int pid) {
    char req[MAX_SIZE], switch_pipe_req[MAX_SIZE];
    sprintf(switch_pipe_req, "../temp/switch_pipe_%d_req", pid);
    sprintf(req, "1 %d ", controller_port);

    return write_request_switch(req, switch_pipe_req);
}

int add_new_connection(int dstn_port, int pid) {
    char req[MAX_SIZE], switch_pipe_req[MAX_SIZE];
    sprintf(switch_pipe_req, "../temp/switch_pipe_%d_req", pid);
    sprintf(req, "2 %d ", dstn_port);

    return write_request_switch(req, switch_pipe_req);
}

int dump_switch_state(int pid) {
    char req[MAX_SIZE], switch_pipe_req[MAX_SIZE];
    sprintf(switch_pipe_req, "../temp/switch_pipe_%d_req", pid);
    strcpy(req, "3");

    return write_request_switch(req, switch_pipe_req);
}

int exit_switch(int pid) {
    char req[MAX_SIZE], switch_pipe_req[MAX_SIZE];
    sprintf(switch_pipe_req, "../temp/switch_pipe_%d_req", pid);
    strcpy(req, "4");

    return write_request_switch(req, switch_pipe_req);
}
