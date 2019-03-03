#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <unistd.h>

#define MAX_SIZE 1024

using namespace std;

int host_id = -1;

int controller_port = -1;
int controller_fd = -1;

int connected_switch_port = -1;
int switch_conn_fd = -1;
int connected_switch_id = -1;

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
		sprintf(buff, "%d", 0);
		send(controller_fd, buff, sizeof buff, 0);
		memset(buff, '\0', sizeof buff);
		recv(controller_fd, buff, MAX_SIZE, 0);
		char *token = strtok(buff, "::");
		token = strtok(NULL, "::");
		host_id = strtol(token, NULL, 10);
		cout << "Host id received from controller: " << host_id << "..." << endl;
		while(1) {
			char msg_from_controller[MAX_SIZE];
			memset(msg_from_controller, '\0', sizeof msg_from_controller);
			recv(controller_fd, msg_from_controller, MAX_SIZE, 0);
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
	recv(switch_conn_fd, buff, MAX_SIZE, 0);
	connected_switch_id = strtol(buff, NULL, 10);
	memset(buff, '\0', sizeof buff);
	sprintf(buff, "%d", host_id);
	send(switch_conn_fd, buff, sizeof buff, 0);
}

void *manage_incoming_connections(void *dummy_arg) {
	while(1) {
		char buff[MAX_SIZE], *msg;
		recv(switch_conn_fd, buff, MAX_SIZE, 0);
		msg = strtok(buff, "::");
		msg = strtok(NULL, "::");
		cout << "Incoming message:" << endl;
		cout << msg << endl;
		struct timeval t;
		gettimeofday(&t, NULL);
		cout << "Time when msg received: " << t.tv_sec << "sec " << t.tv_usec << "usec" << endl;
	}
}

void start_listening() {
	pthread_t connection_manager;
	pthread_create(&connection_manager, NULL, &manage_incoming_connections, NULL);
}

void send_msg(char msg[], int flow_id) {
	char buff[MAX_SIZE];
	sprintf(buff, "%d::%s", flow_id, msg);
	send(switch_conn_fd, buff, sizeof buff, 0);
}

int main() {
	cout << "Starting new host..." << endl;
	cout << "Menu:" << endl;
	cout << "1. Connect to controller" << endl;
	cout << "2. Connect to switch" << endl;
	cout << "3. Send new message" << endl;
	cout << "4. Exit" << endl;
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
			if(connected_switch_port != -1) {
				cout << "Already connected to a switch!" << endl;
				break;
			}
			cout << "Enter switch port: " << endl;
			cin >> connected_switch_port;
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
			string msg_str;
			int flow_id;
			cout << "Enter flow id: ";
			cin >> flow_id;
			cout << "Enter message: ";
			ws(cin);
			getline(cin, msg_str);
			strcpy(msg, msg_str.c_str());
			struct timeval t;
			gettimeofday(&t, NULL);
			cout << "Time when msg sent: " << t.tv_sec << "sec " << t.tv_usec << "usec" << endl;
			send_msg(msg, flow_id);
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
