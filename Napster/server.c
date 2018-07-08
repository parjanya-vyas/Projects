#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ifaddrs.h>
#include <netdb.h>
#include <errno.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>

//Get the string of ip that is associated with the provided interface
char * getIpString(char interface[])
{
	struct ifaddrs *addrs, *tmp;
	getifaddrs(&addrs);//Get information about all the interfaces
	tmp = addrs;

	//Traversing all interfaces to get the right IP, currently only IPv4 supported hence AF_INET
	while (tmp) 
	{
	    if (tmp->ifa_addr && tmp->ifa_addr->sa_family == AF_INET)
	    {
		struct sockaddr_in *pAddr = (struct sockaddr_in *)(tmp->ifa_addr);
		if(strcmp(tmp->ifa_name,interface)==0)//interface name matches
			return inet_ntoa(pAddr->sin_addr);
	    }

	    tmp = tmp->ifa_next;
	}
}

//Every time the listening port is appended to the keyword of the operation to be performed "join", "publish" or "fetch"
//Get the port number after discarding the keyword of operation
void get_client_port_string(char client_port_string[], char operation[], char buff[])
{
	int i, op_len = strlen(operation);
	char *strptr = &buff[0] + op_len;
	for(i=0; strptr!=NULL && *strptr!='\0' && *strptr!='\n' && *strptr!=' '; i++)
		client_port_string[i] = *(strptr++);
	client_port_string[i] = '\0';
}

int main(int argc, char *argv[])
{
	if(argc!=2)
	{
		printf("Error in arguments. Please pass interface name on which server should be running.");
		exit(1);
	}

	if(getIpString(argv[1]) == NULL)//Interface name  specified does not have a valid IP associated with it
	{
		printf("Device is offline on this interface\n");
		exit(1);
	}

	int listen_sockfd, connection_sockfd, childpid;
	struct sockaddr_in server_addr, client_addr;
	char buff[1024];
	socklen_t server_addr_sz, client_addr_sz;

	server_addr.sin_family = AF_INET;
	//Central server always run on port number 7777
	//Because as per Lord Voldemort, '7' is the most powerful magical number :p
	server_addr.sin_port = htons(7777);
	server_addr.sin_addr.s_addr = inet_addr(getIpString(argv[1]));//Get the ip address of interface specified by user
	memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
	server_addr_sz = sizeof server_addr;
	client_addr_sz = sizeof client_addr;

	listen_sockfd = socket(PF_INET, SOCK_STREAM, 0);
	bind(listen_sockfd, (struct sockaddr *) &server_addr, server_addr_sz);
	if(listen(listen_sockfd,5)==0)
	{
		printf("Listening on %s:%u\n",inet_ntoa(server_addr.sin_addr),ntohs(server_addr.sin_port));
	}
	else
	{
		printf("Error in listening!\n");
		perror("listen");
		exit(1);
	}

	while(1)
	{
		connection_sockfd = accept(listen_sockfd, (struct sockaddr *) &client_addr, &client_addr_sz);

		//Accept connections from different clients and serve them in different child processes
		if((childpid=fork())==0)
		{
			close(listen_sockfd);
			//get ip of client from client address struct
			char *client_ip_string = inet_ntoa(client_addr.sin_addr);
			//We cannot get port number from client addr struct because it will give us the port number from which
			//peer has connected right now
			//The port we are interested in is the listening port of peer because that is the port number on which
			//other peers will try to make connections and download files
			//This listening port cannot be extracted from any of structs and is provided by peer itself after the keyword of
			//operation that is "join", "publish" or "fetch"
			char client_port_string[10];

			memset(client_port_string, '\0', sizeof(client_port_string));
			recv(connection_sockfd, buff, 1024, 0);

			if(strstr(buff, "join") != NULL)
			{
				get_client_port_string(client_port_string, "join", buff);//get the listening port number of peer
				printf("Received join from %s:%s\n", client_ip_string, client_port_string);

				//Create a server db to make all the entries of operations requested by various peers
				if(mkdir("./Server/",0777)==-1 && errno != EEXIST)
					perror("Error Creating Directory");
				FILE *fp = fopen("./Server/db","a");
				char string_to_write[30];

				//This new_line_str is only made because
				//due to some unknown reason "\n" was not accepted as a valid string on my machine
				//Hence making a "\n" string manually
				char new_line_str[2];
				new_line_str[0]='\n';
				new_line_str[1]='\0';

				//making a db entry of ip : port to accomplish a join
				strcpy(string_to_write, client_ip_string);
				strcat(string_to_write, " : ");
				strcat(string_to_write, client_port_string);
				strcat(string_to_write, new_line_str);
				fwrite(string_to_write, sizeof(char), strlen(string_to_write), fp);
				fclose(fp);
			}
			else if(strstr(buff, "publish") != NULL)
			{
				get_client_port_string(client_port_string, "publish", buff);//get the listening port number of peer
				printf("Received publish from %s:%s\n", client_ip_string, client_port_string);

				//extract file name from the publish request
				char *str_ptr = &buff[0];
				str_ptr += (strlen("publish")+strlen(client_port_string));

				char new_line_str[2];
				new_line_str[0]='\n';
				new_line_str[1]='\0';
				strcat(str_ptr,new_line_str);

				//Append file name in front of ip : port of the peer
				//One cannot easily edit file in C as trying to addthe file name will overwrite the part of next ip address
				//Hence, creating a new file, copying the contents that are not changed
				//appending the filename in between as required
				//After the operation is sucessfully completed, old db is deleted and new db is renamed so that it replaces
				//old db file.
				FILE *fp = fopen("./Server/db","r");
				FILE *fp_new = fopen("./Server/dbnew","w");
				char *line;
				line = (char *)malloc(1024 * sizeof(char));
				size_t len = 1024;
				char client_ip_port[100];
				strcpy(client_ip_port, client_ip_string);
				strcat(client_ip_port," : ");
				strcat(client_ip_port, client_port_string);
				while(getline(&line, &len, fp) != -1)
				{
					char *cur_ip = strstr(line,client_ip_port);
					if(cur_ip != NULL)
					{
						line[strlen(line)-1]='\0';
						strcat(line, str_ptr);
					}
					fwrite(line, sizeof(char), strlen(line), fp_new);
				}
				free(line);
				fclose(fp);
				fclose(fp_new);
				remove("./Server/db");
				rename("./Server/dbnew","./Server/db");
			}
			else if(strstr(buff, "fetch") != NULL)
			{
				get_client_port_string(client_port_string, "fetch", buff);//get the listening port number of peer
				printf("Received fetch from %s:%s\n", client_ip_string, client_port_string);

				//extract file name from the fetch request
				char *str_ptr = &buff[0];
				str_ptr += (strlen("fetch")+strlen(client_port_string)+1);

				//Find the file name in the db and return the ip : port of the peer containing the file
				//If file not found then merely return "NoFile" keyword
				FILE *fp = fopen("./Server/db","r");
				char *line;
				line = (char *)malloc(1024 * sizeof(char));
				size_t len = 1024;
				char peer_ip_port[100];
				int found = 0;
				while(getline(&line, &len, fp) != -1)
				{
					char *cur_file = strstr(line,str_ptr);
//This check makes sure that indeed the entire file name is contained as a subtring in current line of db. e.g., if the current line
//contains a file named xyz and fetch request is for "xyzxyz" of "xxyz" or "xyzz" then this check will fail as the file is not the one
//that is requested by the peer. Entire file name must match.
					if(cur_file != NULL && (*(cur_file+strlen(str_ptr))==' ' || *(cur_file+strlen(str_ptr))=='\n'))
					{
						found++;
						int i, space_cnt=0;
						//peer ip and port are stored in format <ip> : <port> hence,
						//Everything from start of line till the third space is copied in peer ip and port string
						for(i=0;(line[i]!=' ')||(++space_cnt<3);i++)
							peer_ip_port[i] = line[i];
						peer_ip_port[i] = '\0';
						break;
					}
				}
				free(line);
				fclose(fp);
				if(found<=0)//If file is not found then just return "NoFile" to the peer
					strcpy(peer_ip_port, "NoFile");
				send(connection_sockfd, peer_ip_port, sizeof(peer_ip_port), 0);
			}
			else
				printf("Invalid request!\n");
			close(connection_sockfd);
			
			return 0;
		}
		else
			close(connection_sockfd);
	}

	return 0;
}
