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

void start_listening() {
	int listen_sockfd;
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

	if(fork() == 0) {
		while(1) {
			struct sockaddr_in client_addr;
			socklen_t client_addr_sz;
			client_addr_sz = sizeof client_addr;
			int connection_sockfd = accept(listen_sockfd, (struct sockaddr *) &client_addr, &client_addr_sz);
			
			char buff[MAX_SIZE];
			recv(connection_sockfd, buff, MAX_SIZE, 0);
			cout << "Incoming message:" << endl;
			cout << buff << endl;
			close(connection_sockfd);
		}
	}
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
	while(1) {
		int ch;
		cout << "Menu:" << endl;
		cout << "1. Connect to switch" << endl;
		cout << "2. Send new message" << endl;
		cin >> ch;
		switch(ch) {
		case 1: {
			if(connected_switch_port != -1) {
				cout << "Already connected to a switch!";
				break;
			}
			cout << "Enter switch port: " << endl;
			cin >> connected_switch_port;
			cout << "Establishing connection..." << endl;
			start_listening();
			break;
		}
		case 2: {
			if(connected_switch_port != -1) {
				cout << "Please connect to a switch!" << endl;
				break;
			}
			char msg[MAX_SIZE];
			int flow_id;
			cout << "Enter message: ";
			fgets(msg, MAX_SIZE, stdin);
			cout << "Enter flow id: ";
			cin >> flow_id;
			send_msg(msg, flow_id);
			break;
		}
		default: {
			cout << "Invalid choice!" << endl;
			break; 
		}
		}
	}

	return 0;
}
