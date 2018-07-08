void encrypt(unsigned char msg[], unsigned char enc[], NUM key, int sz)
{
    for(int i=0;i<sz;i++)
        enc[i] = msg[i] + key;
}

void send_to_ot(unsigned char msg1[], unsigned char msg2[], int sz1, int sz2)
{
    NUM a = rand() % P, A = pow_mod_p(G, a), B, key0, key1;
    unsigned char g_a[8], g_b[8];
    unsigned char enc1[1024], enc2[1024];
    int enable = 1;
    convert_to_char(A, g_a);

    int listenfd, connfd;
    struct sockaddr_in serv_addr;
    char buff[1024];
    socklen_t addrlen = sizeof serv_addr;
    listenfd = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(7777);
    serv_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serv_addr.sin_zero, '\0', sizeof serv_addr.sin_zero);
    bind(listenfd, (struct sockaddr *) &serv_addr, addrlen);
    listen(listenfd, 5);

    connfd = accept(listenfd, (struct sockaddr *)&serv_addr, &addrlen);

    setsockopt(connfd, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int));

    send(connfd, g_a, sizeof g_a, 0);

    recv(connfd, g_b, sizeof g_b, 0);
    close(connfd);

    B = convert_to_int(g_b);
    key0 = pow_mod_p(B,a);
    key1 = pow_mod_p(B/A,a);

    encrypt(msg1, enc1, key0, sz1);
    encrypt(msg2, enc2, key1, sz2);

    connfd = accept(listenfd, (struct sockaddr *)&serv_addr, &addrlen);
    setsockopt(connfd, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int));

    send(connfd, enc1, sz1, 0);
    close(connfd);
    connfd = accept(listenfd, (struct sockaddr *)&serv_addr, &addrlen);
    setsockopt(connfd, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int));

    send(connfd, enc2, sz2, 0);

    close(connfd);
    close(listenfd);
}
