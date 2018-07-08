#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>

#define P 83
#define G 81

typedef unsigned long long int NUM;

NUM pow_mod_p(NUM y, NUM x)
{
    NUM ans=1;
    for(NUM i=0;i<x;i++)
        ans=((ans*y)%P);

    return ans;
}

void convert_to_char(NUM num, unsigned char ans[])
{
    for(int i = 56,j = 0; j < 8; i-=8,j++)
        ans[j] = (num >> i) & 0xFF;
}

NUM convert_to_int(unsigned char num[])
{
    NUM ans=0;
    for(int i=0;i<8;i++)
    {
        ans<<=8;
        ans|=num[i];
    }

    return ans;
}
