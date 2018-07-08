#include <stdio.h>
#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <conio.h>
#include <math.h>

#define key1 "1000101011100111000"
#define key2 "0110101100110010101100"
#define key3 "11100111110001010110001"

using namespace std;

char msg[4096], *processed;
int MSG_SZ = 0;

int XOR(int x, int y)
{
	return (x^y);
}

int shift_right(int buff[], char REG[])
{
	int i=0,sz,first_bit,out;
	if(strcmp(REG,"REG1")==0)
	{
		sz=19;
		first_bit=XOR(buff[18],buff[17]);
		first_bit=XOR(first_bit,buff[16]);
		first_bit=XOR(first_bit,buff[13]);
	}
	else if(strcmp(REG,"REG2")==0)
	{
		sz=22;
		first_bit=XOR(buff[21],buff[20]);
	}
	else if(strcmp(REG,"REG3")==0)
	{
		sz=23;
		first_bit=XOR(buff[22],buff[21]);
		first_bit=XOR(first_bit,buff[20]);
		first_bit=XOR(first_bit,buff[7]);
	}
	else
	{
		printf("Error!");
		return -1;
	}
	out = buff[sz-1];
//	cout << first_bit << endl;
	for(i=sz-1;i>0;i--)
		buff[i] = buff[i-1];
	buff[0]=first_bit;
	return out;
}

int maj(int buff1[], int buff2[], int buff3[])
{
	if((buff1[8]&&buff2[10])||(buff1[8]&&buff3[10])||(buff2[10]&&buff3[10]))
		return 1;
	else
		return 0;
}

void encrypt(int buff1[], int buff2[], int buff3[])
{
	char temp[2];
	processed = (char *)malloc(MSG_SZ*sizeof(char));
	int i=0, cur_bit, act_bit, maj_bit, out1, out2, out3;
	for(i=0;i<MSG_SZ;i++)
	{
		temp[0]=msg[i];
		temp[1]='\0';
		act_bit=atoi(temp);
		maj_bit=maj(buff1,buff2,buff3);
		if(buff1[8]==maj_bit)
			out1=shift_right(buff1,"REG1");
		else
			out1=buff1[18];
		if(buff2[10]==maj_bit)
			out2=shift_right(buff2,"REG2");
		else
			out2=buff2[21];
		if(buff3[10]==maj_bit)
			out3=shift_right(buff3,"REG3");
		else
			out3=buff3[22];
		cur_bit=XOR(out1,out2);
		cur_bit=XOR(cur_bit,out3);
		cur_bit=XOR(cur_bit,act_bit);
		if(cur_bit==0)
			processed[i]='0';
		if(cur_bit==1)
			processed[i]='1';
	}
//	for(i=0;i<MSG_SZ;i++)
//		printf("%c",processed[i]);
}

int convert_char_binary(char ch, char buff[])
{
	int bin_num = (int)ch, sz=0;
	for(int i=0;i<8;i++)
	{
		if((bin_num)&((int)pow((double)2,i)))
			buff[sz++]='1';
		else
			buff[sz++]='0';
	}

	return sz;
}

void convert_binary(char org_msg[],int len)
{
	int cur_char_len = 0, j=0;
	char cur_char[8];
	for(int i=0;i<len;i++)
	{
		cur_char_len = convert_char_binary(org_msg[i],cur_char);
		for(j=0;j<cur_char_len;j++)
			msg[MSG_SZ+j]=cur_char[j];
		MSG_SZ+=cur_char_len;
	}
}

char convert(char *str)
{
	char out=0;
	if(strlen(str)<8)
	{
		for(int i=0;i<8-strlen(str);i++)
			strcat(str,"0");
	}
	for(int i=0;i<8;i++)
	{
		if(str[i]=='0')
			out|=0;
		else
			out|=(int)(pow(double(2),i));
	}
	return out;
}


int main()
{
	char temp[2], path[1024], ch, org_msg[14096], choice;
	int buff1[19], buff2[22], buff3[23];
	int i=0,len = 0;
	FILE *fin, *fout;
	memset(msg,'\0',4096);
	for(i=0;i<19;i++)
	{
		temp[0]=key1[i];
		temp[1]='\0';
		buff1[i]=atoi(temp);
	}
	for(i=0;i<22;i++)
	{
		temp[0]=key2[i];
		temp[1]='\0';
		buff2[i]=atoi(temp);
	}
	for(i=0;i<23;i++)
	{
		temp[0]=key3[i];
		temp[1]='\0';
		buff3[i]=atoi(temp);
	}
	printf("Enter path of the file that is to be encrypted or decrypted:");
	gets(path);
	fin = fopen(path,"r");
	if(fin)
	{
		while((ch = getc(fin))!=EOF)
			org_msg[len++] = ch;
	}
	else
	{
		cout << "Error in opening input file!";
		getch();
		return 1;
	}
	convert_binary(org_msg,len);
	encrypt(buff1,buff2,buff3);
	cout << "Press 'E' for encryption, 'D' for decryption" << endl;
	cout << "Enter choice:";
	cin >> choice;
	char *temp_string;
	char out_file_name[1024];
	strcpy(out_file_name,path);
	if((choice=='E')||(choice=='e'))
		strcat(out_file_name,".enc");
	else
		strcat(out_file_name,".dec");
	fout = fopen(out_file_name,"w");
	for(int i=0;i<MSG_SZ;i+=8)
	{
		temp_string = &processed[i];
		ch = convert(temp_string);
		putc(ch,fout);
	}
	fclose(fin);
	fclose(fout);
	cout << "Operation Completed! Press Enter to continue...";
	getch();

	return 0;
}
