#include <iostream>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <pthread.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>

#include "../sha256/sha256.h"

#define MAX_SIZE 1024

#define CONTROLLER_PIPE_REQ "../temp/controller_pipe_req"
#define CONTROLLER_PIPE_RES "../temp/controller_pipe_res"
#define CONTROLLER_LOG "../logs/controller_logs.txt"

using namespace std;

int secure_controller = 0;

int listen_sockfd;
int listening_port;

pthread_mutex_t connection_mutex = PTHREAD_MUTEX_INITIALIZER;
int p2p_connection_fds[MAX_SIZE];
int switch_listen_ports[MAX_SIZE];
string all_hashes[MAX_SIZE];
int num_switches_connected = 0;

int flows[MAX_SIZE][2];
int num_flows = 0;

//src_switch_id x dstn_switch_id
int all_connections[MAX_SIZE][2];
int num_connections = 0;

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
	int pipefd = open(CONTROLLER_PIPE_REQ, O_RDONLY);
	int count = read(pipefd, inp, MAX_SIZE);
	close(pipefd);

	return count;
}

int write_response(char *res) {
	int pipefd = open(CONTROLLER_PIPE_RES, O_WRONLY);
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

int parse_line(char* line){
    // This assumes that a digit will be found and the line ends in " Kb".
    int i = strlen(line);
    const char* p = line;
    while (*p <'0' || *p > '9') p++;
    line[i-3] = '\0';
    i = atoi(p);
    return i;
}

int get_memory_usage_value(){ //Note: this value is in KB!
    FILE* file = fopen("/proc/self/status", "r");
    int result = -1;
    char line[128];

    while (fgets(line, 128, file) != NULL){
        if (strncmp(line, "VmSize:", 7) == 0){
            result = parse_line(line);
            break;
        }
    }
    fclose(file);
    return result;
}

void dump_state() {
	cout << "Controller type: " << (secure_controller==0?"Normal":"Secure") << endl;
	cout << "Current memory used: " << get_memory_usage_value() << " KB" << endl;
	pthread_mutex_lock(&connection_mutex);
	cout << "Number of switches connected: " << num_switches_connected << endl;
	cout << "Network Topology:" << endl;
	cout << "Src switch Id\tDstn switch Id" << endl;
	for(int i=0;i<num_switches_connected;i++)
		cout << all_connections[i][0] << "\t" << all_connections[i][1] << endl;
	pthread_mutex_unlock(&connection_mutex);
	cout << "Number of flows: " << num_flows << endl;
	cout << "All flows:" << endl;
	cout << "Source\tDestination" << endl;
	for(int i=0;i<num_flows;i++)
		cout << flows[i][0] << "\t" << flows[1] << endl;
	if(secure_controller == 1) {
		cout << "All hashes:" << endl;
		cout << "Switch Id\tHash Value" << endl;
		for(int i=0;i<num_switches_connected;i++)
			cout << i << "\t" << all_hashes[i] << endl;
	}
}

void close_all_sockets() {
	for(int i=0;i<num_switches_connected;i++)
		close(p2p_connection_fds[i]);
	close(listen_sockfd);
}

void *manage_switch_connections(void *thread_arg) {
	int switch_id = *((int *)thread_arg);
	int conn_fd = p2p_connection_fds[switch_id];

	char buff[MAX_SIZE];
	while(1) {
		memset(buff, '\0', sizeof buff);
		int rcvd_data = receive_msg(conn_fd, buff, MAX_SIZE, 0);
		if(rcvd_data == 0)
			continue;
		cout << "Message received from switch " << switch_id << " : " << buff << endl;
		int cmd = strtol(strtok(buff, "::"), NULL, 10);

		switch(cmd) {
		case 0: {
			cout << "Ack received..." << endl;
			cout << "Network data received: " << rcvd_data << " Bytes" << endl;
			if(secure_controller == 1) {
				char *token = strtok(NULL, "::");
				cout << "Hash received in acknowledgment:" << token << endl;
				if(token == NULL) {
					cout << "NULL hash received!!!" << endl;
					break;
				}
				pthread_mutex_lock(&connection_mutex);
				if(string(token) != all_hashes[switch_id])
					cout << "Corrupt Switch!" << endl;
				else
					cout << "Honest Switch!" << endl;
				pthread_mutex_unlock(&connection_mutex);
			}
			break;
		}
		case 1: {
			int dstn_switch_id = strtol(strtok(NULL, "::"), NULL, 10);
			cout << "Registering new connection between " << switch_id << " and " << dstn_switch_id << endl;
			pthread_mutex_lock(&connection_mutex);
			all_connections[num_connections][0] = switch_id;
			all_connections[num_connections][1] = dstn_switch_id;
			num_connections++;
			pthread_mutex_unlock(&connection_mutex);
			break;
		}
		default: {
			cout << "Invalid message from switch " << switch_id << endl;
			return NULL;
		}
		}

	}
}

void *start_listening(void *dummy_arg) {
	struct sockaddr_in server_addr;
	char buff[1024];
	socklen_t server_addr_sz;

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(0);
	server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
	server_addr_sz = sizeof server_addr;

	listen_sockfd = socket(AF_INET, SOCK_STREAM, 0);
	bind(listen_sockfd, (struct sockaddr *) &server_addr, server_addr_sz);
	getsockname(listen_sockfd, (struct sockaddr *) &server_addr, &server_addr_sz);
	if(listen(listen_sockfd, 128)==0) {
		char res[MAX_SIZE];
		listening_port = ntohs(server_addr.sin_port);
		sprintf(res, "%d", listening_port);
		write_response(res);
		cout << "Controller listening on port " << listening_port << endl;
	}
	else {
		cout << "Error in listening!" << endl;
		exit(1);
	}

	while(1) {
		struct sockaddr_in client_addr;
		socklen_t client_addr_sz;
		client_addr_sz = sizeof client_addr;
		int connection_sockfd = accept(listen_sockfd, (struct sockaddr *) &client_addr, &client_addr_sz);
		cout << "New switch connected with controller..." << endl;

		char msg_from_switch[MAX_SIZE];
		receive_msg(connection_sockfd, msg_from_switch, MAX_SIZE, 0);
		int cur_switch_listen_port = strtol(msg_from_switch, NULL, 10);

		pthread_mutex_lock(&connection_mutex);
		int cur_switch_id = num_switches_connected++;
		cout << "Switch ID: " << cur_switch_id << endl;
		if(secure_controller == 1) {
			all_hashes[cur_switch_id] = sha256(to_string(cur_switch_id));
			cout << "Hash for switch " << cur_switch_id << " calculated and initialized as: " << all_hashes[cur_switch_id] << endl;
		}
		p2p_connection_fds[cur_switch_id] = connection_sockfd;
		switch_listen_ports[cur_switch_id] = cur_switch_listen_port;
		pthread_mutex_unlock(&connection_mutex);

		char buff[MAX_SIZE];
		sprintf(buff, "#2::%d#", cur_switch_id);
		send(connection_sockfd, buff, strlen(buff)+1, 0);

		pthread_t connection_manager;
		int *thread_arg = (int *)malloc(sizeof(int *));
		*thread_arg = cur_switch_id;
		pthread_create(&connection_manager, NULL, &manage_switch_connections, (void *)thread_arg);
	}
}

void add_new_flow(int path_len, int path[]) {
	int total_nw_data = 0;
	struct timeval start, end;
	gettimeofday(&start, NULL);
	int cur_flow_id = num_flows++;
	flows[cur_flow_id][0] = path[0];
	flows[cur_flow_id][1] = path[path_len-1];
	cout << "Flow Id for the new flow: " << cur_flow_id << endl;
	pthread_mutex_lock(&connection_mutex);
	for(int i=1; i<path_len-1;i++) {
		char buff[MAX_SIZE], rcv_buff[MAX_SIZE];
		sprintf(buff, "#1::%d::%d::%d#", cur_flow_id, path[i-1], path[i+1]);
		if(secure_controller == 1) {
			string new_hash_inp = all_hashes[path[i]] + to_string(cur_flow_id) + "::" + to_string(path[i-1]) + "::" + to_string(path[i+1]);
			cout << "Refreshing hash..." << endl;
			all_hashes[path[i]] = sha256(new_hash_inp);
			cout << "New hash calculated for switch " << path[i] << ":" << all_hashes[path[i]] << endl;
		}
		total_nw_data += send(p2p_connection_fds[path[i]], buff, strlen(buff)+1, 0);
	}
	pthread_mutex_unlock(&connection_mutex);
	gettimeofday(&end, NULL);
	unsigned long long t = 1000000 * (end.tv_sec - start.tv_sec) + (end.tv_usec - start.tv_usec);
    char time_and_data[MAX_SIZE];
    sprintf(time_and_data, "%llu %d", t, total_nw_data);
    write_response(time_and_data);
	cout << "Time taken to add new flow: " << t << "us" << endl;
	cout << "Total bytes sent: " << total_nw_data << endl;
}

int main(int argc, char *argv[]) {
	mkfifo(CONTROLLER_PIPE_REQ, 0666);
	mkfifo(CONTROLLER_PIPE_RES, 0666);
	freopen(CONTROLLER_LOG, "w", stdout);
	if(argc != 2) {
		cout << "Invalid arguments!" << endl;
		return 1;
	}
	secure_controller = strtol(argv[1], NULL, 10);

	cout << "Starting controller..." << endl;
	pthread_t listener_thread;
	pthread_create(&listener_thread, NULL, &start_listening, NULL);

	cout << "Menu" << endl;
	cout << "1. Add new flow" << endl;
	cout << "2. Dump controller state" << endl;
	cout << "3. Exit" << endl;

	while(1) {
		char args[MAX_SIZE];
		read_request(args);
		char **args_arr = (char**)malloc(MAX_SIZE * sizeof(char*));
        int num_args = get_args_from_input(args_arr, args);
		int ch = strtol(args_arr[0], NULL, 10);
		switch(ch) {
		case 1: {
			int path_len = strtol(args_arr[1], NULL, 10);
			int *path = (int *)malloc(path_len * sizeof(int));
			for(int i=0;i<path_len;i++)
				path[i] = strtol(args_arr[2+i], NULL, 10);
			add_new_flow(path_len, path);
			break;
		}
		case 2: {
			dump_state();
			break;
		}
		case 3: {
			close_all_sockets();
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
