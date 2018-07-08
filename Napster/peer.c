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

//The listening port of this peer to which, connection can be established to download files that this peer has
char my_listening_port[10];

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

//Get file name that is to be extracted from user's input
void extract_file_name_to_fetch(char file_name[], char choice[])
{
	int i,j;
	for(i=2,j=0;choice[i]!='\n';i++)
	{
		//Ignore slashes that precede white space because C accepts file names with white spaces
		//Here user is asked to enter a slash before white space in order to maintain symmetry with "publish" operation
		//Otherwise, space is not needed at all
		if(choice[i]!='\\')
			file_name[j++]=choice[i];
	}
	file_name[j]='\0';
}

//When a fetch is encountered, this function connects to the peer with file and downloads the file via TCP connection
void get_file_from_peer(char peer_ip[], char peer_port[], char file_name[])
{
	int sockfd;
	struct sockaddr_in peer_addr;
	socklen_t peer_addr_sz;
	char buff[1024];
	char path[100], slash_str[2];

	//This slash_str is only made because due to some unknown reason "/" was not accepted as a valid string on my machine
	//Hence making a "/" string manually
	//For every peer a specific folder is maintained to keep its downloaded and published file
	//This folder is specified my "Peer" followed by its port number to distinguish all peers
	slash_str[0] = '/';
	slash_str[1] = '\0';
	strcpy(path, "./Peer");
	strcat(path, my_listening_port);
	strcat(path, slash_str);
	if(mkdir(path,0777)==-1 && errno != EEXIST)//If Peer folder already exists than EEXIST is thrown which is to be ignored
		perror("Error Creating Directory");
	strcat(path, file_name);

	peer_addr.sin_family = AF_INET;
	peer_addr.sin_port = htons(atoi(peer_port));
	peer_addr.sin_addr.s_addr = inet_addr(peer_ip);
	memset(peer_addr.sin_zero, '\0', sizeof peer_addr.sin_zero);
	peer_addr_sz = sizeof(peer_addr);

	sockfd = socket(PF_INET, SOCK_STREAM, 0);
	if(connect(sockfd, (struct sockaddr *) &peer_addr, peer_addr_sz)!=0)
	{
		perror("Fetch Peer connect");
		exit(1);
	}
	send(sockfd, file_name, 1024, 0);

	FILE *file_to_write = fopen(path, "w");

	memset(buff,'\0',1024);

	printf("\nStarting file transfer for %s\n",file_name);

	while(1)
	{
		int received = recv(sockfd, buff, 1024, 0);
		if(received==0)//When file transfer completes, connection is terminated from other end which results in recv returning 0
			break;
		fwrite(buff, sizeof(char), received, file_to_write);
	}
	printf("\nFile transfer for %s completed successfully!\n",file_name);

	fclose(file_to_write);
	close(sockfd);
}

//Used for extracting file name and file path for "publish" operation from user input
void prepare_file_name_and_path(char file_name[], char file_path[], char choice[])
{
	int i, j;
	char slash_str[2];
	slash_str[0] = '/';
	slash_str[1] = '\0';
	//Run the loop till first space is encountered which is not in the file name itself
	//If the space is in file name then user will be entering a '\' before it and hence we will know that it is in the file name
	for(i=2, j=0;choice[i]!=' ' || choice[i-1]=='\\';i++)
	{
		if(choice[i]!='\\')//The '\' character should not be present in file name or path while giving it to fopen
			file_name[j++]=choice[i];
	}
	file_name[j]='\0';
	for(i=i+1, j=0;choice[i]!='\0';i++)
	{
		if(choice[i]!='\\')
			file_path[j++] = choice[i];
	}
	file_path[j] = '\0';
	if(file_path[strlen(file_path)-1]=='\n')//File path should not end with a new line character
		file_path[strlen(file_path)-1]='\0';
	//If the file path does not contain the file name, then we need to add it after the containing directory's name
	//The second check in if makes sure that file name indeed is inside the file path and not just a substring of file name
	//e.g., if file name is "xyz" then "xyz" file is indeed present at the end of file path and not
	//"xxyzz" or "xyzxyz" or something like that
	if(strstr(file_path,file_name)==NULL || *strstr(file_path,file_name)!=file_path[strlen(file_path)-strlen(file_name)])
	{
		if(file_path[strlen(file_path)-1]=='/')//If '/' is present at the end then just append file name
			strcat(file_path, file_name);
		else//If not then add a '/' and then append the file name
		{
			strcat(file_path, slash_str);
			strcat(file_path, file_name);
		}
	}
}

//Copy the given file from its given path to the folder "Peer<port_number>" to make it ready to be published
void copy_file(char src_path[], char dstn_path[])
{
	FILE *src_fp = fopen(src_path,"r");
	FILE *dstn_fp = fopen(dstn_path, "w");
	if(src_fp==NULL)
		printf("Source null");
	if(dstn_fp==NULL)
		printf("Dstn null");
	char buff[1024];
	int sz = fread(buff, sizeof(char), 1024, src_fp);
	while(sz>0)
	{
		fwrite(buff, sizeof(char), sz, dstn_fp);
		sz = fread(buff, sizeof(char), 1024, src_fp);
	}
	fclose(src_fp);
	fclose(dstn_fp);
}

//Start the server part of this peer to make it ready to accept connections from other peers and upload the file
void start_listening(char *interface)
{
	if(getIpString(interface) == NULL)//The interface that user provided does not have a valid IP assosciated with it
	{
		printf("Device is offline on this interface\n");
		exit(1);
	}

	int listen_sockfd, connection_sockfd;
	struct sockaddr_in my_addr, peer_addr;
	char buff[1024], path[100], slash_str[2];
	socklen_t my_addr_sz, peer_addr_sz;

	my_addr_sz = sizeof my_addr;
	peer_addr_sz = sizeof peer_addr;
	my_addr.sin_family = AF_INET;
	my_addr.sin_port = 0;
	my_addr.sin_addr.s_addr = inet_addr(getIpString(interface));
	memset(my_addr.sin_zero, '\0', sizeof my_addr.sin_zero);
	slash_str[0] = '/';
	slash_str[1] = '\0';

	listen_sockfd = socket(PF_INET, SOCK_STREAM, 0);
	bind(listen_sockfd, (struct sockaddr *) &my_addr, my_addr_sz);
	//The random port chosen by kernel to bind the socket is needed to be passed to central server
	//So that other peers can find it and download files from it
	//Hence, this port is saved in the string my_listening_port which is a global variable and will be passed to central server
	//when join will be performed
	getsockname(listen_sockfd, (struct sockaddr *) &my_addr, &my_addr_sz);
	sprintf(my_listening_port,"%u",ntohs(my_addr.sin_port));
	//Now that the server port is bound the server process is needed to be isolated from the client part, hence the fork
	if(fork()==0)
	{
		if(listen(listen_sockfd,5)!=0)
		{
			printf("Error in listening!\n");
			perror("listen");
			exit(1);
		}

		while(1)
		{
			//Accept connections from peers and serve them in forked child processes
			connection_sockfd = accept(listen_sockfd, (struct sockaddr *) &peer_addr, &peer_addr_sz);
			if(fork()==0)
			{
				close(listen_sockfd);
				recv(connection_sockfd,buff,sizeof buff, 0);
				strcpy(path,"./Peer");
				strcat(path,my_listening_port);
				strcat(path,slash_str);
				strcat(path,buff);

				//Uploading file to the peer that asked for it
				FILE *file_to_send = fopen(path,"r");

				while(!feof(file_to_send))
				{
					int sz = fread(buff, sizeof(char), 1024, file_to_send);
					send(connection_sockfd, buff, sz, 0);
				}

				fclose(file_to_send);
				close(connection_sockfd);

				exit(0);
			}
			else
				close(connection_sockfd);
		}

	}
	else
		close(listen_sockfd);
}

int main(int argc, char *argv[])
{
	if(argc!=4)
	{
	    printf("Supply server ip address, followed by server port number followed by name of interface on which you are listening\n");
            printf("Type \"ifconfig\" and press <enter> to know the names of all available interfaces on your device\n");
	    exit(0);
	}

	//Start listening on the interface that user has passed
	start_listening(argv[3]);

	//Establish a connection with central server to automatically do the Join operation
	int sockfd;
	struct sockaddr_in server_addr;
	char buff[1024];
	socklen_t server_addr_sz;
	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(atoi(argv[2]));
	server_addr.sin_addr.s_addr = inet_addr(argv[1]);
	memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
	server_addr_sz = sizeof server_addr;

	if((sockfd = socket(PF_INET, SOCK_STREAM, 0))==-1)
		perror("Join socket");
	if(connect(sockfd, (struct sockaddr *) &server_addr, server_addr_sz)==-1)
		perror("Join connect");
	strcpy(buff,"join");
	//Server needs to know the listening port of this peer. Port number of current socket is of no use because
	//It is going to get closed after every operation join, publish or fetch and new port number randomly will
	//get assigned to every connection. Only port number that matters to other peer as well as central server is the port
	//on which the listening is done. So that other peers can find this peer to download file from it.
	//Hence, passing the listening port along with keyword "join"
	strcat(buff,my_listening_port);
	printf("\nJoining network...\n");
	send(sockfd, buff, sizeof(buff), 0);
	printf("\nYou have joined the p2p network successfully\n");
	close(sockfd);
	while(1)
	{
		char choice[1024];
		//Menu provided to user so that publish or fetch can be chosen or user can exit from the network
		//For publish, to correctly identify and differentiate file name from file path a blank space is required in between them
		//Now if file name itself contains a blank space then this will create a problem
		//To differentiate the space of file name from the space present in between file name and path
		//all the spaces that occurr in name or path are to be preceeded by a "\"
		//This is only required in "publish" as it has 2 arguments name and path
		//But in order to maintain symmetry, all the spaces in file name and path are to be preceeded by a "\"
		//This will prevent confusion from naive user's side and increase user satisfaction
		printf("\nChoose one of the following options:\n");
		printf("NOTE: For files and folders containing spaces in their names,\n      Enter a \"\\\" before the space\n");
		printf("1) To Publish a file that you want to share,\n   Enter \"1 <filename> <absolute filepath>\"\n");
		printf("2) To Download a file from any of the peers,\n   Enter \"2 <filename>\"\n");
		printf("3) To exit from network,\n   Enter \"3\"\n");
		printf("Enter your choice : ");
		fgets(choice, 1024, stdin);
		if(choice[0] == '3')
		{
			printf("\nI hope you were satisfied by the network performance!\nBbye!See you next time!\n");
			break;
		}
		else if(fork()==0)
		{
			if(choice[0] == '1')
			{
				char file_name[1024], file_path[1024];

				//extract file name and path from user input
				prepare_file_name_and_path(file_name, file_path, choice);

				char destn_file_path[1024], slash_str[2];
				slash_str[0] = '/';
				slash_str[1] = '\0';
				strcpy(destn_file_path, "./Peer");
				strcat(destn_file_path, my_listening_port);
				strcat(destn_file_path, slash_str);
				if(mkdir(destn_file_path,0777)==-1 && errno != EEXIST)
						perror("Error Creating Directory");
				strcat(destn_file_path, file_name);

				//copy the file from its path to the Peer folder
				copy_file(file_path, destn_file_path);
				char string_to_send[1024], blank_space[2];

				//blank_space string also follows the same logic as given for slash_str earlier
				blank_space[0] = ' ';
				blank_space[1] = '\0';

				//Send keyword "publish" followed by the listening port number followed by the file name to publish
				strcpy(string_to_send, "publish");
				strcat(string_to_send, my_listening_port);
				strcat(string_to_send, blank_space);
				strcat(string_to_send, file_name);

				if((sockfd = socket(PF_INET, SOCK_STREAM, 0))==-1)
					perror("Publish socket");
				if(connect(sockfd, (struct sockaddr *) &server_addr, server_addr_sz)==-1)
					perror("Publish connect");
				if(send(sockfd, string_to_send, sizeof(string_to_send), 0)>0)
					printf("\nFile %s is ready to be uploaded\n",file_name);
				close(sockfd);
			}
			else if(choice[0] == '2')
			{
				char file_name[1024];

				//extract file name from user input
				extract_file_name_to_fetch(file_name, choice);

				char string_to_send[1024], blank_space[2], server_reply[1024];
				blank_space[0] = ' ';
				blank_space[1] = '\0';

				//similar to publish, send keyword "fetch" followed by listening port and file name to be fetched
				strcpy(string_to_send, "fetch");
				strcat(string_to_send, my_listening_port);
				strcat(string_to_send, blank_space);
				strcat(string_to_send, file_name);

				printf("\nFetching IP of peer containing %s\n", file_name);

				//Request server for the ip and port of peer containing <file_name>
				if((sockfd = socket(PF_INET, SOCK_STREAM, 0))==-1)
					perror("Fetch socket");
				if(connect(sockfd, (struct sockaddr *) &server_addr, server_addr_sz)==-1)
					perror("Fetch connect");
				send(sockfd, string_to_send, sizeof(string_to_send), 0);

				if(recv(sockfd, server_reply, sizeof(server_reply), 0)>0)
				{
					close(sockfd);

					//If server says NoFile than the file is non existent in the network
					if(strcmp(server_reply, "NoFile")!=0)
					{
						//extract ip and port of peer from server's reply message
						char ip_string[20], port_string[10], *str_ptr;
						int i;
						for(i=0;server_reply[i]!=' ';i++)
							ip_string[i] = server_reply[i];
						ip_string[i] = '\0';
						str_ptr = &server_reply[0] + (strlen(ip_string)+strlen(" : "));
						strcpy(port_string, str_ptr);
						printf("\nAsking peer for %s\n", file_name);

						//Get file from the peer suggested by the server
						get_file_from_peer(ip_string, port_string, file_name);
					}
					else
						printf("\nFile not found in the network\n");
				}
			}
			else
				printf("\nInvalid choice, Please try again!\n");
			return 0;
		}
	}

	return 0;
}
