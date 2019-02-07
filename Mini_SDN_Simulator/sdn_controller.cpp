#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <pthread.h>
#include <unistd.h>

#define MAX_SIZE 1024

using namespace std;

int secure_controller = 0;

pthread_mutex_t connection_mutex = PTHREAD_MUTEX_INITIALIZER;
int listen_sockfd;
int p2p_connection_fds[MAX_SIZE];
int num_switches_connected = 0;

int flows[MAX_SIZE][2];
int num_flows = 0;

void dump_state() {
	pthread_mutex_lock(&connection_mutex);
	cout << "Number of switches connected: " << num_switches_connected << endl;
	pthread_mutex_unlock(&connection_mutex);
	cout << "Number of flows: " << num_flows << endl;
	cout << "All flows:" << endl;
	cout << "Source\tDestination" << endl;
	for(int i=0;i<num_flows;i++)
		cout << flows[i][0] << "\t" << flows[1] << endl;
}

void close_all_sockets() {
	for(int i=0;i<num_switches_connected;i++)
		close(p2p_connection_fds[i]);
	close(listen_sockfd);
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
	if(listen(listen_sockfd,5)==0)
		cout << "Controller listening on port " << ntohs(server_addr.sin_port) << endl;
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
		pthread_mutex_lock(&connection_mutex);
		cout << "Switch ID: " << num_switches_connected << endl;
		p2p_connection_fds[num_switches_connected++] = connection_sockfd;
		pthread_mutex_unlock(&connection_mutex);
	}
}

void add_new_flow() {
	int path_len;
	int path[MAX_SIZE], path_ports[MAX_SIZE];
	cout << "Enter path length: ";
	cin >> path_len;
	cout << "Enter all path nodes' ids (space separated):" << endl;
	for(int i=0;i<path_len;i++)
		cin >> path[i];
	cout << "Enter all path nodes' listening ports (space separated):" << endl;
	for(int i=0;i<path_len;i++)
		cin >> path_ports[i];
	int cur_flow_id = num_flows++;
	flows[cur_flow_id][0] = path[0];
	flows[cur_flow_id][1] = path[path_len-1];
	cout << "Flow Id for the new flow: " << cur_flow_id << endl;
	pthread_mutex_lock(&connection_mutex);
	for(int i=1; i<path_len-1;i++) {
		char buff[MAX_SIZE];
		sprintf(buff, "1::%d::%d::%d", cur_flow_id, path_ports[i], path_ports[i+1]);
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

	while(1) {
		int ch;
		cout << "Menu" << endl;
		cout << "1. Add new flow" << endl;
		cout << "2. Dump controller state" << endl;
		cout << "3. Exit" << endl;
		cout << "Enter choice: ";
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
