#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>

#define MAX_SIZE 1024
#define RANDOM_MSG_SIZE 512

#define PIPE_PREFIX "../temp/"
#define LOG_PREFIX "../logs/"

using namespace std;

int host_id = -1;

int controller_port = -1;
int controller_fd = -1;

int connected_switch_port = -1;
int switch_conn_fd = -1;
int connected_switch_id = -1;

char pipename_req[MAX_SIZE], pipename_res[MAX_SIZE], pipename_msg[MAX_SIZE];

int receive_msg(int sockfd, char *buf, size_t len, int flags) {
	char msg[MAX_SIZE];
	int i, ret = recv(sockfd, msg, len, flags);
	cout << "Received raw msg:" << msg << endl;
	if(msg[0]!='#')
		return 0;
	for(i=1;i<ret && msg[i]!='#';i++)
		buf[i-1] = msg[i];
	buf[i-1] = '\0';
	return (ret-2);
}

int read_request(char *inp) {
	int pipefd = open(pipename_req, O_RDONLY);
	int count = read(pipefd, inp, MAX_SIZE);
	close(pipefd);

	return count;
}

int write_response(char *res) {
	int pipefd = open(pipename_res, O_WRONLY);
	int count = write(pipefd, res, strlen(res)+1);
	close(pipefd);

	return count;
}

int write_msg(char *res) {
	int pipefd = open(pipename_msg, O_WRONLY);
	int count = write(pipefd, res, strlen(res)+1);
	close(pipefd);

	return count;
}

int get_args_from_input(char **args, char *req) {
    memset(args, '\0', sizeof(char*) * MAX_SIZE);
    char *curToken = strtok(req, " ");
    int i;
    for (i = 0; curToken != NULL; i++) {
      args[i] = strdup(curToken);
      curToken = strtok(NULL, " ");
    }

    return i;
}

void get_random_msg(char *msg) {
	for(int i=0;i<RANDOM_MSG_SIZE;i++)
		msg[i] = ('0' + rand()%10);
	msg[RANDOM_MSG_SIZE] = '\0';
}

void *connect_to_controller(void *dummy_arg) {
	cout << "Trying to connect controller at 127.0.0.1:" << controller_port << "..." << endl;

	struct sockaddr_in controller_addr;
	socklen_t controller_addr_sz;

	controller_addr.sin_family = AF_INET;
	controller_addr.sin_port = htons(controller_port);
	controller_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(controller_addr.sin_zero, '\0', sizeof controller_addr.sin_zero);
	controller_addr_sz = sizeof controller_addr;

	controller_fd = socket(AF_INET, SOCK_STREAM, 0);
	if(connect(controller_fd, (struct sockaddr *) &controller_addr, controller_addr_sz) == 0) {
		cout << "Connected to controller successfully..." << endl;
		char buff[MAX_SIZE];
		sprintf(buff, "#%d#", 0);
		send(controller_fd, buff, sizeof buff, 0);
		memset(buff, '\0', sizeof buff);
		receive_msg(controller_fd, buff, MAX_SIZE, 0);
		char *token = strtok(buff, "::");
		token = strtok(NULL, "::");
		host_id = strtol(token, NULL, 10);
		cout << "Host id received from controller: " << host_id << "..." << endl;
		while(1) {
			char msg_from_controller[MAX_SIZE];
			memset(msg_from_controller, '\0', sizeof msg_from_controller);
			receive_msg(controller_fd, msg_from_controller, MAX_SIZE, 0);
			cout << "Received new message from controller:" << endl;
			cout << msg_from_controller << endl;
		}
	}
	else
		cout << "Error connecting controller!" << endl;
}

void connect_to_switch() {
	struct sockaddr_in server_addr;
	socklen_t server_addr_sz;

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(connected_switch_port);
	server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
	server_addr_sz = sizeof server_addr;

	switch_conn_fd = socket(AF_INET, SOCK_STREAM, 0);
	if(connect(switch_conn_fd, (struct sockaddr *) &server_addr, server_addr_sz) == 0)
		cout << "Sucessfully connected to the switch..." << endl;
	else
		perror("Switch connection error");
	getsockname(switch_conn_fd, (struct sockaddr *) &server_addr, &server_addr_sz);

	char buff[MAX_SIZE];
	receive_msg(switch_conn_fd, buff, MAX_SIZE, 0);
	connected_switch_id = strtol(buff, NULL, 10);
	memset(buff, '\0', sizeof buff);
	sprintf(buff, "#%d#", host_id);
	send(switch_conn_fd, buff, sizeof buff, 0);
}

void *manage_incoming_connections(void *dummy_arg) {
	while(1) {
		char buff[MAX_SIZE], *msg, pipe_buff[MAX_SIZE];
		int sz = receive_msg(switch_conn_fd, buff, MAX_SIZE, 0);
		struct timeval t;
		gettimeofday(&t, NULL);
		msg = strtok(buff, "::");
		msg = strtok(NULL, "::");
		cout << "Incoming message:" << endl;
		cout << msg << endl;
		cout << "Time when msg received: " << t.tv_sec << " sec " << t.tv_usec << " usec" << endl;
		cout << "Message size: " << sz << " Bytes" << endl;
		sprintf(pipe_buff, "%ld %ld %s", t.tv_sec, t.tv_usec, msg);
		write_msg(pipe_buff);
	}
}

void start_listening() {
	pthread_t connection_manager;
	pthread_create(&connection_manager, NULL, &manage_incoming_connections, NULL);
}

void send_msg(char msg[], int flow_id) {
	char buff[MAX_SIZE];
	sprintf(buff, "#%d::%s#", flow_id, msg);
	send(switch_conn_fd, buff, sizeof buff, 0);
}

int main() {
	sprintf(pipename_req, "%shost_pipe_%d_req", PIPE_PREFIX, getpid());
	sprintf(pipename_res, "%shost_pipe_%d_res", PIPE_PREFIX, getpid());
	sprintf(pipename_msg, "%shost_pipe_%d_msg", PIPE_PREFIX, getpid());
	mkfifo(pipename_req, 0666);
	mkfifo(pipename_res, 0666);
	mkfifo(pipename_msg, 0666);
	char logfile[MAX_SIZE];
	sprintf(logfile, "%shost_log%d.txt", LOG_PREFIX, getpid());
	freopen(logfile, "w", stdout);

	cout << "Starting new host..." << endl;
	cout << "Menu:" << endl;
	cout << "1. Connect to controller" << endl;
	cout << "2. Connect to switch" << endl;
	cout << "3. Send new message" << endl;
	cout << "4. Exit" << endl;
	srand(time(NULL));
	while(1) {
		char args[MAX_SIZE];
		read_request(args);
		char **args_arr = (char**)malloc(MAX_SIZE * sizeof(char*));
        int num_args = get_args_from_input(args_arr, args);
		int ch = strtol(args_arr[0], NULL, 10);
		switch(ch) {
		case 1: {
			pthread_t controller_manager;
			controller_port = strtol(args_arr[1], NULL, 10);
			pthread_create(&controller_manager, NULL, &connect_to_controller, NULL);
			break;
		}
		case 2: {
			if(connected_switch_port != -1) {
				cout << "Already connected to a switch!" << endl;
				break;
			}
			connected_switch_port = strtol(args_arr[1], NULL, 10);
			cout << "Establishing connection..." << endl;
			connect_to_switch();
			start_listening();
			break;
		}
		case 3: {
			if(connected_switch_port == -1) {
				cout << "Please connect to a switch!" << endl;
				break;
			}
			char msg[MAX_SIZE];
			get_random_msg(msg);
			//string msg_str;
			int flow_id = strtol(args_arr[1], NULL, 10);
			//cout << "Enter message: ";
			//ws(cin);
			//getline(cin, msg_str);
			//strcpy(msg, msg_str.c_str());
			struct timeval t;
			gettimeofday(&t, NULL);
			send_msg(msg, flow_id);
            cout << "Time when msg sent: " << t.tv_sec << "sec " << t.tv_usec << "usec" << endl;
            char time_sent[MAX_SIZE];
            sprintf(time_sent, "%ld %ld", t.tv_sec, t.tv_usec);
            write_response(time_sent);
            
			break;
		}
		case 4: {
			close(switch_conn_fd);
			return 0;
		}
		default: {
			cout << "Invalid choice!" << endl;
			break; 
		}
		}
	}

	return 0;
}
