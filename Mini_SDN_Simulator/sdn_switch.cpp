#include <iostream>
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

int secure_switch = 0;

int controller_port = -1;
int controller_fd;

pthread_mutex_t flow_table_mutex = PTHREAD_MUTEX_INITIALIZER;
int flow_table[MAX_SIZE][3];
int num_flow_table_entries = 0;

int switch_port_fds[MAX_SIZE];
int switch_port_listening[MAX_SIZE];
int num_switch_ports = 0;

void dump_state() {
	cout << "Switch type: " << (secure_switch==0?"Normal":"Secure") << endl;
	cout << "Number of ports in switch: " << num_switch_ports << endl;
	cout << "Each listening on:" << endl;
	for(int i=0;i<num_switch_ports;i++)
		cout << switch_port_listening[i] << endl;
	pthread_mutex_lock(&flow_table_mutex);
	cout << "Number of flow table entries: " << num_flow_table_entries << endl;
	cout << "Flow table:" << endl;
	cout << "Flow id\tSource Port\tDestination Port" << endl;
	for(int i=0;i<num_flow_table_entries;i++)
		cout << flow_table[i][0] << "\t" << flow_table[i][1] << "\t" << flow_table[i][2] << "\t" << endl;
	pthread_mutex_unlock(&flow_table_mutex);
	cout << "Connected to controller at port: " << controller_port << endl;
}

void close_all_sockets() {
	if(controller_port != -1)
		close(controller_fd);
	for(int i=0;i<num_switch_ports;i++)
		close(switch_port_fds[i]);
}

void add_new_flow_table_entry(int flow_id, int src_port, int dstn_port) {
	pthread_mutex_lock(&flow_table_mutex);
	flow_table[num_flow_table_entries][0] = flow_id;
	flow_table[num_flow_table_entries][1] = src_port;
	flow_table[num_flow_table_entries][2] = dstn_port;
	num_flow_table_entries++;
	pthread_mutex_unlock(&flow_table_mutex);
}

int get_dstn_port_from_flow_table(int flow_id, int src_port) {
	int dstn_port = -1;
	pthread_mutex_lock(&flow_table_mutex);
	for(int i=0;i<num_flow_table_entries;i++) {
		if(flow_table[i][0] == flow_id && flow_table[i][1] == src_port)
			dstn_port = flow_table[i][2];
	}
	pthread_mutex_unlock(&flow_table_mutex);

	return dstn_port;
}

void route_packet(char packet[], int src_port) {
	char packet_to_send[MAX_SIZE];
	strcpy(packet_to_send, packet);
	char *token = strtok(packet, "::");
	int flow_id = strtol(token, NULL, 10);
	int dstn_port = get_dstn_port_from_flow_table(flow_id, src_port);
	cout << "Found the destination port " << dstn_port << " from flow table..." << endl;

	int sockfd;
	struct sockaddr_in server_addr;
	socklen_t server_addr_sz;

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(dstn_port);
	server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
	server_addr_sz = sizeof server_addr;

	cout << "Forwarding packet to " << dstn_port << "..." << endl;
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	connect(sockfd, (struct sockaddr *) &server_addr, server_addr_sz);
	send(sockfd, packet_to_send, sizeof packet_to_send, 0);
	close(sockfd);
}

void *add_new_switch_port(void *dummy_arg) {
	int listen_fd_id = num_switch_ports++;
	struct sockaddr_in temp_addr, final_addr, peer_addr;
	socklen_t temp_addr_sz, peer_addr_sz;

	temp_addr.sin_family = AF_INET;
	temp_addr.sin_port = htons(0);
	temp_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(temp_addr.sin_zero, '\0', sizeof temp_addr.sin_zero);
	temp_addr_sz = sizeof temp_addr;
	peer_addr_sz = sizeof peer_addr;

	switch_port_fds[listen_fd_id] = socket(AF_INET, SOCK_STREAM, 0);
	bind(switch_port_fds[listen_fd_id], (struct sockaddr *) &temp_addr, temp_addr_sz);
	listen(switch_port_fds[listen_fd_id], 5);

	getsockname(switch_port_fds[listen_fd_id], (struct sockaddr *) &final_addr, &temp_addr_sz);
	switch_port_listening[listen_fd_id] = ntohs(final_addr.sin_port);
	cout << "Started new switch port on 127.0.0.1:" << switch_port_listening[listen_fd_id] << "..." << endl;

	while(1) {
		int conn_fd = accept(switch_port_fds[listen_fd_id], (struct sockaddr *) &peer_addr, &peer_addr_sz);
		char buff[MAX_SIZE];
		memset(buff, '\0', sizeof buff);
		recv(conn_fd, buff, MAX_SIZE, 0);
		cout << "Received New message at port " << switch_port_listening[listen_fd_id] << ":" << endl;
		cout << buff << endl;
		cout << "Routing Packet..." << endl;
		route_packet(buff, switch_port_listening[listen_fd_id]);
		close(conn_fd);
	}
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
	if(argc != 2) {
		cout << "Error in arguments!" << endl;
		return 1;
	}
	secure_switch = strtol(argv[1], NULL, 10);
	cout << "Starting switch..." << endl;
	cout << "Press 1 to connect to controller" << endl;
	cout << "Press 2 to add new port" << endl;
	cout << "Press 3 to dump switch state" << endl;
	cout << "Press 4 to exit" << endl;

	while(1) {
		int ch;
		cin >> ch;
		switch(ch) {
		case 1: {
			pthread_t controller_manager;
			cout << "Enter controller port: ";
			cin >> controller_port;
			pthread_create(&controller_manager, NULL, &connect_to_controller, NULL);
			break;
		}
		case 2: {
			pthread_t port_manager;
			pthread_create(&port_manager, NULL, &add_new_switch_port, NULL);
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
