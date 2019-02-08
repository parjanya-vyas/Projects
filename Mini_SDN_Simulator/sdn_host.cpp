#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>

#define MAX_SIZE 1024

using namespace std;

int connected_switch_port = -1;
int listen_sockfd;

void *manage_incoming_connections(void *dummy_arg) {
	while(1) {
		struct sockaddr_in client_addr;
		socklen_t client_addr_sz;
		client_addr_sz = sizeof client_addr;
		int connection_sockfd = accept(listen_sockfd, (struct sockaddr *) &client_addr, &client_addr_sz);
		
		char buff[MAX_SIZE], *msg;
		recv(connection_sockfd, buff, MAX_SIZE, 0);
		msg = strtok(buff, "::");
		msg = strtok(NULL, "::");
		cout << "Incoming message:" << endl;
		cout << msg << endl;
		close(connection_sockfd);
	}
}

void start_listening() {
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
		cout << "Host listening on port " << ntohs(server_addr.sin_port) << endl;
	else {
		cout << "Error in listening!" << endl;
		exit(1);
	}

	pthread_t connection_manager;
	pthread_create(&connection_manager, NULL, &manage_incoming_connections, NULL);
}

void send_msg(char msg[], int flow_id) {
	char buff[MAX_SIZE];
	sprintf(buff, "%d::%s", flow_id, msg);

	int sockfd;
	struct sockaddr_in server_addr;
	socklen_t server_addr_sz;

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(connected_switch_port);
	server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
	server_addr_sz = sizeof server_addr;

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	connect(sockfd, (struct sockaddr *) &server_addr, server_addr_sz);
	send(sockfd, buff, sizeof buff, 0);
	close(sockfd);
}

int main() {
	cout << "Starting new host..." << endl;
	cout << "Menu:" << endl;
	cout << "1. Connect to switch" << endl;
	cout << "2. Send new message" << endl;
	cout << "3. Exit" << endl;
	while(1) {
		int ch;
		cin >> ch;
		switch(ch) {
		case 1: {
			if(connected_switch_port != -1) {
				cout << "Already connected to a switch!" << endl;
				break;
			}
			cout << "Enter switch port: " << endl;
			cin >> connected_switch_port;
			cout << "Establishing connection..." << endl;
			start_listening();
			break;
		}
		case 2: {
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
			send_msg(msg, flow_id);
			break;
		}
		case 3: {
			close(listen_sockfd);
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
