void decrypt(unsigned char enc[], unsigned char msg[], NUM key, int sz)
{
    for(int i=0;i<sz;i++)
        msg[i] = enc[i] - key;
}

void receive_from_ot(int ch, unsigned char msg[])
{
    NUM b = rand() % P, A, key;
    unsigned char g_a[8], g_b[8];
    unsigned char msg1[1024], msg2[1024];
    int sz1 = 0, sz2 = 0, sz = 0, enable = 1;

    int sockfd;
    struct sockaddr_in serv_addr;
    unsigned char buff[1024];
    socklen_t addrlen = sizeof serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(7777);
    serv_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serv_addr.sin_zero, '\0', sizeof serv_addr.sin_zero);
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int));
    connect(sockfd, (struct sockaddr *)&serv_addr, addrlen);

    recv(sockfd, g_a, sizeof g_a, 0);

    A = convert_to_int(g_a);
    if(ch==0)
        convert_to_char(pow_mod_p(G, b), g_b);
    else
        convert_to_char(A * pow_mod_p(G, b), g_b);

    send(sockfd, g_b, sizeof g_b, 0);

    key = pow_mod_p(A,b);

    memset(buff,'\0',sizeof buff);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    connect(sockfd, (struct sockaddr *)&serv_addr, addrlen);

    while((sz = recv(sockfd, buff, sizeof buff, 0))>0)
    {
        for(int i=sz1;i<(sz1+sz);i++)
            msg1[i] = buff[i-sz1];
        sz1+=sz;
    }
    close(sockfd);
    sleep(1);

    memset(buff,'\0',sizeof buff);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    connect(sockfd, (struct sockaddr *)&serv_addr, addrlen);

    while((sz = recv(sockfd, buff, sizeof buff, 0))>0)
    {
        for(int i=sz2;i<(sz2+sz);i++)
            msg2[i] = buff[i-sz2];
        sz2+=sz;
    }
    close(sockfd);

    if(ch==0)
        decrypt(msg1, msg, key, sz1);
    else
        decrypt(msg2, msg, key, sz2);
}
