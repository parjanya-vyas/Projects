#include <iostream>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <pthread.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>

#include "../sha256/sha256.h"

#define PIPE_PREFIX "../temp/"
#define LOG_PREFIX "../logs/"

#define MAX_SIZE 1024

using namespace std;

int secure_switch = 0;
string recent_hash;

int switch_id = -1;
int controller_port = -1;
int controller_fd;

pthread_mutex_t flow_table_mutex = PTHREAD_MUTEX_INITIALIZER;
int flow_table[MAX_SIZE][3];
int num_flow_table_entries = 0;

int ngb_fds[MAX_SIZE];
int ngb_ids[MAX_SIZE];
int num_ngb = 0;

int listen_fd = -1;
int listen_port = -1;

char pipename_req[MAX_SIZE], pipename_res[MAX_SIZE];

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
	cout << "Switch type: " << (secure_switch==0?"Normal":"Secure") << endl;
	cout << "Current memory used: " << get_memory_usage_value() << " KB" << endl;
	cout << "Number of connections in switch: " << 	num_ngb << endl;
	cout << "All neighnours ID:" << endl;
	for(int i=0;i<num_ngb;i++)
		cout << ngb_ids[i] << endl;
	pthread_mutex_lock(&flow_table_mutex);
	cout << "Number of flow table entries: " << num_flow_table_entries << endl;
	cout << "Flow table:" << endl;
	cout << "Flow id\tSource ID\tDestination ID" << endl;
	for(int i=0;i<num_flow_table_entries;i++)
		cout << flow_table[i][0] << "\t" << flow_table[i][1] << "\t" << flow_table[i][2] << "\t" << endl;
	pthread_mutex_unlock(&flow_table_mutex);
	cout << "Connected to controller at port: " << controller_port << endl;
	if(secure_switch == 1)
		cout << "Recent Hash: " << recent_hash << endl;
}

void close_all_sockets() {
	if(controller_port != -1)
		close(controller_fd);
}

void add_new_flow_table_entry(int flow_id, int src_id, int dstn_id) {
	pthread_mutex_lock(&flow_table_mutex);
	flow_table[num_flow_table_entries][0] = flow_id;
	flow_table[num_flow_table_entries][1] = src_id;
	flow_table[num_flow_table_entries][2] = dstn_id;
	num_flow_table_entries++;
	pthread_mutex_unlock(&flow_table_mutex);
}

int get_dstn_fd(int flow_id, int src_id) {
	int dstn_fd = -1;
	pthread_mutex_lock(&flow_table_mutex);
	for(int i=0;i<num_flow_table_entries;i++) {
		if(flow_table[i][0] == flow_id && flow_table[i][1] == src_id) {
			int dstn_id = flow_table[i][2];
			cout << "Found the destination id " << dstn_id << " from flow table..." << endl;
			for(int j=0;j<num_ngb;j++)
				if(ngb_ids[j] == dstn_id)
					dstn_fd = ngb_fds[j];
		}
	}
	pthread_mutex_unlock(&flow_table_mutex);

	return dstn_fd;
}

void route_packet(char packet[], int src_id) {
	char packet_to_send[MAX_SIZE];
	strcpy(packet_to_send, packet);
	char *token = strtok(packet, "::");
	int flow_id = strtol(token, NULL, 10);
	int dstn_fd = get_dstn_fd(flow_id, src_id);

	cout << "Forwarding packet..." << endl;
	send(dstn_fd, packet_to_send, sizeof packet_to_send, 0);
}

void *manage_connections(void *thread_arg) {
	int cur_num_ngb = *((int *)thread_arg);
	int conn_fd = ngb_fds[cur_num_ngb];
	int cur_ngb_id = ngb_ids[cur_num_ngb];

	cout << "Connection established between " << switch_id << " and " << cur_ngb_id << "..." << endl;

	char buff[MAX_SIZE];
	while(1) {
		memset(buff, '\0', sizeof buff);
		recv(conn_fd, buff, MAX_SIZE, 0);
		cout << "Received New message from " << cur_ngb_id << ":" << endl;
		cout << buff << endl;
		cout << "Routing Packet..." << endl;
		route_packet(buff, cur_ngb_id);
	}
}

void *start_listening(void *dummy_arg) {
	struct sockaddr_in temp_addr;
	socklen_t temp_addr_sz;

	temp_addr.sin_family = AF_INET;
	temp_addr.sin_port = htons(0);
	temp_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(temp_addr.sin_zero, '\0', sizeof temp_addr.sin_zero);
	temp_addr_sz = sizeof temp_addr;

	listen_fd = socket(AF_INET, SOCK_STREAM, 0);
	bind(listen_fd, (struct sockaddr *) &temp_addr, temp_addr_sz);
	listen(listen_fd, 128);

	getsockname(listen_fd, (struct sockaddr *) &temp_addr, &temp_addr_sz);
	listen_port = ntohs(temp_addr.sin_port);
	char res[MAX_SIZE];
	sprintf(res, "%d", listen_port);
	write_response(res);
	cout << "Switch accepting connections on 127.0.0.1:" << listen_port << "..." << endl;

	while(1) {
		struct sockaddr_in peer_addr;
		socklen_t peer_addr_sz;
		peer_addr_sz = sizeof peer_addr;
		int conn_fd = accept(listen_fd, (struct sockaddr *) &peer_addr, &peer_addr_sz);

		char buff[MAX_SIZE];
		sprintf(buff, "%d", switch_id);
		send(conn_fd, buff, sizeof buff, 0);
		memset(buff, '\0', sizeof buff);
		recv(conn_fd, buff, MAX_SIZE, 0);

		ngb_fds[num_ngb] = conn_fd;
		ngb_ids[num_ngb] = strtol(buff, NULL, 10);

		sprintf(buff, "1::%d", ngb_ids[num_ngb]);
		send(controller_fd, buff, sizeof buff, 0);

		pthread_t connection_manager;
		int *thread_arg = (int *)malloc(sizeof(int *));
		*thread_arg = num_ngb;
		pthread_create(&connection_manager, NULL, &manage_connections, (void *)thread_arg);

		num_ngb++;
	}
}

void add_new_connection(int dstn_port) {
	struct sockaddr_in peer_addr, cur_addr;
	socklen_t peer_addr_sz, cur_addr_sz;

	peer_addr.sin_family = AF_INET;
	peer_addr.sin_port = htons(dstn_port);
	peer_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(peer_addr.sin_zero, '\0', sizeof peer_addr.sin_zero);
	peer_addr_sz = sizeof peer_addr;
	cur_addr_sz = sizeof cur_addr;

	int new_conn_fd = socket(AF_INET, SOCK_STREAM, 0);
	connect(new_conn_fd, (struct sockaddr *) &peer_addr, peer_addr_sz);

	getsockname(new_conn_fd, (struct sockaddr *) &cur_addr, &cur_addr_sz);
	cout << "New connection added between " << ntohs(cur_addr.sin_port) << " and " << ntohs(peer_addr.sin_port) << "..." << endl;

	char buff[MAX_SIZE];
	recv(new_conn_fd, buff, MAX_SIZE, 0);
	int cur_ngb_id = strtol(buff, NULL, 10);
	sprintf(buff, "%d", switch_id);
	send(new_conn_fd, buff, sizeof buff, 0);

	ngb_fds[num_ngb] = new_conn_fd;
	ngb_ids[num_ngb] = cur_ngb_id;

	pthread_t connection_manager;
	int *thread_arg = (int *)malloc(sizeof(int *));
	*thread_arg = num_ngb;
	pthread_create(&connection_manager, NULL, &manage_connections, (void *)thread_arg);

	num_ngb++;
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
		char msg_to_controller[MAX_SIZE];
		sprintf(msg_to_controller, "%d", listen_port);
		send(controller_fd, msg_to_controller, sizeof msg_to_controller, 0);
		while(1) {
			char msg_from_controller[MAX_SIZE];
			memset(msg_from_controller, '\0', sizeof msg_from_controller);
			recv(controller_fd, msg_from_controller, MAX_SIZE, 0);
			cout << "Received new message from controller:" << endl;
			cout << msg_from_controller << endl;
			char *token = strtok(msg_from_controller, "::");
			int command = strtol(token, NULL, 10);
			switch(command) {
			case 1: {
				cout << "Adding new flow rule..." << endl;
				token = strtok(NULL, "::");
				int flow_id = strtol(token, NULL, 10);
				token = strtok(NULL, "::");
				int src_port = strtol(token, NULL, 10);
				token = strtok(NULL, "::");
				int dstn_port = strtol(token, NULL, 10);
				cout << "Flow id: " << flow_id << " Src port: " << src_port << " Dstn port: " << dstn_port << endl;
				add_new_flow_table_entry(flow_id, src_port, dstn_port);
				string new_flow_rule = to_string(flow_id) + "::" + to_string(src_port) + "::" + to_string(dstn_port);
				recent_hash = sha256(recent_hash + new_flow_rule);
				char ack_msg[MAX_SIZE];
				if(secure_switch == 1)
					sprintf(ack_msg, "0::%s", recent_hash.c_str());
				else
					sprintf(ack_msg, "0");
				send(controller_fd, ack_msg, sizeof ack_msg, 0);
				break;
			}
			case 2: {
				cout << "Initializing switch id..." << endl;
				token = strtok(NULL, "::");
				switch_id = strtol(token, NULL, 10);
				cout << "Switch id received from controller: " << switch_id << "..." << endl;
				if(secure_switch == 1) {
					recent_hash = sha256(to_string(switch_id));
					cout << "Hash calculated and initialized: " << recent_hash << endl;
				}
				break;
			}
			default: {
				cout << "Invalid message from controller!" << endl;
				close(controller_fd);
				return NULL;
			}
			}
		}
	}
	else
		cout << "Error connecting controller!" << endl;
}

int main(int argc, char *argv[]) {
	sprintf(pipename_req, "%sswitch_pipe_%d_req", PIPE_PREFIX, getpid());
	sprintf(pipename_res, "%sswitch_pipe_%d_res", PIPE_PREFIX, getpid());
	mkfifo(pipename_req, 0666);
	mkfifo(pipename_res, 0666);
	char logfile[MAX_SIZE];
	sprintf(logfile, "%sswitch_log%d.txt", LOG_PREFIX, getpid());
	freopen(logfile, "w", stdout);
	if(argc != 2) {
		cout << "Error in arguments!" << endl;
		return 1;
	}
	secure_switch = strtol(argv[1], NULL, 10);
	cout << "Starting switch..." << endl;
	pthread_t listener_thread;
	pthread_create(&listener_thread, NULL, &start_listening, NULL);
	cout << "Press 1 to connect to controller" << endl;
	cout << "Press 2 to add new connection" << endl;
	cout << "Press 3 to dump switch state" << endl;
	cout << "Press 4 to exit" << endl;

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
			int dstn_port = strtol(args_arr[1], NULL, 10);
			add_new_connection(dstn_port);
			break;
		}
		case 3: {
			dump_state();
			break;
		}
		case 4: {
			cout << "Shutting down..." << endl;
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
