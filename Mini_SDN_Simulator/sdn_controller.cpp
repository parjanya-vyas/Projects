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

#include "sha256.h"

#define MAX_SIZE 1024

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

void dump_state() {
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
		recv(conn_fd, buff, MAX_SIZE, 0);
		int cmd = strtol(strtok(buff, "::"), NULL, 10);

		switch(cmd) {
		case 0: {
			cout << "Ack received..." << endl;
			if(secure_controller == 1) {
				char *token = strtok(NULL, "::");
				cout << "Hash received in acknowledgement:" << token << endl;
				if(string(token) != all_hashes[switch_id])
					cout << "Corrupt Switch!" << endl;
				else
					cout << "Honest Switch!" << endl;
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
	if(listen(listen_sockfd,5)==0) {
		listening_port = ntohs(server_addr.sin_port);
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
		recv(connection_sockfd, msg_from_switch, MAX_SIZE, 0);
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
		sprintf(buff, "2::%d", cur_switch_id);
		send(connection_sockfd, buff, sizeof buff, 0);

		pthread_t connection_manager;
		int *thread_arg = (int *)malloc(sizeof(int *));
		*thread_arg = cur_switch_id;
		pthread_create(&connection_manager, NULL, &manage_switch_connections, (void *)thread_arg);
	}
}

void add_new_flow() {
	int path_len;
	int path[MAX_SIZE];
	cout << "Enter path length: ";
	cin >> path_len;
	cout << "Enter all path nodes' ids (space separated):" << endl;
	for(int i=0;i<path_len;i++)
		cin >> path[i];
	int cur_flow_id = num_flows++;
	flows[cur_flow_id][0] = path[0];
	flows[cur_flow_id][1] = path[path_len-1];
	cout << "Flow Id for the new flow: " << cur_flow_id << endl;
	pthread_mutex_lock(&connection_mutex);
	for(int i=1; i<path_len-1;i++) {
		char buff[MAX_SIZE], rcv_buff[MAX_SIZE];
		sprintf(buff, "1::%d::%d::%d", cur_flow_id, path[i-1], path[i+1]);
		if(secure_controller == 1) {
			string new_hash_inp = all_hashes[path[i]] + to_string(cur_flow_id) + "::" + to_string(path[i-1]) + "::" + to_string(path[i+1]);
			cout << "Refreshing hash..." << endl;
			all_hashes[path[i]] = sha256(new_hash_inp);
			cout << "New hash calculated for switch " << path[i] << ":" << all_hashes[path[i]] << endl;
		}
		send(p2p_connection_fds[path[i]], buff, sizeof buff, 0);
	}
	pthread_mutex_unlock(&connection_mutex);
}

int main(int argc, char *argv[]) {
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
		int ch;
		cin >> ch;
		switch(ch) {
		case 1: {
			add_new_flow();
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
