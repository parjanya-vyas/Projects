#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>

#define MAX_SIZE 1024

void message_handler(int pid, char *msg, unsigned long long recv_time_sec, unsigned long long recv_time_usec);

int write_request_host(char *req, char *host_pipe_req) {
	int pipefd = open(host_pipe_req, O_WRONLY);
	int count = write(pipefd, req, strlen(req)+1);
	close(pipefd);

	return count;
}

int read_response_host(char *res, char *host_pipe_res) {
	int pipefd = open(host_pipe_res, O_RDONLY);
	int count = read(pipefd, res, MAX_SIZE);
	close(pipefd);

	return count;
}

void *start_listening(void *args) {
	int pid = *((int *)args);
	free(args);
	char pipename_msg[MAX_SIZE];
	sprintf(pipename_msg, "../temp/host_pipe_%d_msg", pid);

	while(1) {
		char res[MAX_SIZE], *next_ptr;
		read_response_host(res, pipename_msg);
		unsigned long long recv_time_sec = strtoll(res, &next_ptr, 10);
		unsigned long long recv_time_usec = strtoll(next_ptr+1, &next_ptr, 10);
		message_handler(pid, next_ptr+1, recv_time_sec, recv_time_usec);
	}
}

int start_host() {
    int pid = fork();
    if(pid != 0) {
    	char pipename_req[MAX_SIZE], pipename_res[MAX_SIZE], pipename_msg[MAX_SIZE];
    	sprintf(pipename_req, "../temp/host_pipe_%d_req", pid);
		sprintf(pipename_res, "../temp/host_pipe_%d_res", pid);
		sprintf(pipename_msg, "../temp/host_pipe_%d_msg", pid);
		mkfifo(pipename_req, 0666);
		mkfifo(pipename_res, 0666);
		mkfifo(pipename_msg, 0666);

		int *arg = new int;
		*arg = pid;
		pthread_t connection_manager;
		pthread_create(&connection_manager, NULL, &start_listening, arg);

        return pid;
    } else {
        char * const args[]={(char *)"./sdn_host", (char *)NULL};
        execvp(args[0], args);
        return 0;
    }
}

int connect_host_to_controller(int controller_port, int pid) {
    char req[MAX_SIZE], host_pipe_req[MAX_SIZE];
    sprintf(host_pipe_req, "../temp/host_pipe_%d_req", pid);
    sprintf(req, "1 %d ", controller_port);

    return write_request_host(req, host_pipe_req);
}

int connect_host_to_switch(int switch_port, int pid) {
    char req[MAX_SIZE], host_pipe_req[MAX_SIZE];
    sprintf(host_pipe_req, "../temp/host_pipe_%d_req", pid);
    sprintf(req, "2 %d ", switch_port);

    return write_request_host(req, host_pipe_req);
}

int send_random_msg(int flow_id, unsigned long long *sent_time_sec, unsigned long long *sent_time_usec, int pid) {
    char req[MAX_SIZE], time_sent[MAX_SIZE], *usec_ptr, host_pipe_req[MAX_SIZE], host_pipe_res[MAX_SIZE];
    sprintf(host_pipe_req, "../temp/host_pipe_%d_req", pid);
    sprintf(host_pipe_res, "../temp/host_pipe_%d_res", pid);
    sprintf(req, "3 %d", flow_id);

    write_request_host(req, host_pipe_req);
    read_response_host(time_sent, host_pipe_res);

    *sent_time_sec = strtoll(time_sent, &usec_ptr, 10);
    *sent_time_usec = strtoll(usec_ptr+1, NULL, 10);

    return 0;
}

int exit_host(int pid) {
    char req[MAX_SIZE], host_pipe_req[MAX_SIZE];
    sprintf(host_pipe_req, "../temp/host_pipe_%d_req", pid);
    strcpy(req, "4");

    return write_request_host(req, host_pipe_req);
}
