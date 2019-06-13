#include <iostream>

#include "../interfaces/controller_interface.h"
#include "../interfaces/switch_interface.h"
#include "../interfaces/host_interface.h"

#define SLEEP_TIME 50000

using namespace std;

typedef struct time_sec_usec {
	unsigned long long sec, usec;
} TIME;

typedef struct network_element_info {
	int pid, port, id;
} NET_ELEM;

int secure_sdn, num_hosts;
int controller_port;
NET_ELEM switches[MAX_SIZE], hosts1[MAX_SIZE], hosts2[MAX_SIZE];

TIME sent_time[MAX_SIZE];
unsigned long long all_msg_times[MAX_SIZE];

int get_host_index_from_pid_for_recv_time(int pid, int *host_bit) {
	for(int i=0;i<num_hosts;i++) {
		if(hosts1[i].pid == pid) {
			*host_bit = 1;
			return i;
		}
	}

	for(int i=0;i<num_hosts;i++) {
		if(hosts2[i].pid == pid) {
			*host_bit = 2;
			return i;
		}
	}

	return -1;
}

void message_handler(int pid, char *msg, unsigned long long recv_time_sec, unsigned long long recv_time_usec) {
	int host_bit;
	int host_index = get_host_index_from_pid_for_recv_time(pid, &host_bit);
	if(host_index == -1) {
		cout << "Invalid pid received!" << endl;
		return;
	}

	int flow_id;
	if(host_index == 0)
		flow_id = 0;
	else {
		flow_id = (2 * host_index - 1);
		if(host_bit == 2)
			flow_id++;
	}

	all_msg_times[flow_id] = 1000000 * (recv_time_sec - sent_time[flow_id].sec)
			+ (recv_time_usec - sent_time[flow_id].usec);
	cout << "Message propagation time for flow " << flow_id << ": " << all_msg_times[host_index] << "usec" << endl;
}

int main(int argc, char *argv[]) {
	if(argc != 3) {
		cout << "Incorrect arguments!" << endl;
		cout << "Proper usage: ./topology_2 <secure bit> <number of hosts>" << endl;

		return 0;
	}

	secure_sdn = strtol(argv[1], NULL, 10);
	num_hosts = strtol(argv[2], NULL, 10);

	controller_port = start_controller(secure_sdn);

	cout << "Starting " << num_hosts << " switches and connecting them to controller..." << endl;
	for(int i=0;i<num_hosts;i++) {
		switches[i].pid = start_switch(secure_sdn, &switches[i].port);
		connect_switch_to_controller(controller_port, switches[i].pid);
		switches[i].id = i;
		usleep(SLEEP_TIME);
	}

	int cur_id = num_hosts;

	cout << "Starting " << 2*num_hosts << " hosts and connecting a different pair to each switch..." << endl;
	for(int i=0;i<num_hosts;i++) {
		hosts1[i].pid = start_host();
		connect_host_to_controller(controller_port, hosts1[i].pid);
		usleep(SLEEP_TIME);
		connect_host_to_switch(switches[i].port, hosts1[i].pid);
		usleep(SLEEP_TIME);
		hosts1[i].id = cur_id++;

		hosts2[i].pid = start_host();
		connect_host_to_controller(controller_port, hosts2[i].pid);
		usleep(SLEEP_TIME);
		connect_host_to_switch(switches[i].port, hosts2[i].pid);
		usleep(SLEEP_TIME);
		hosts2[i].id = cur_id++;

		cout << "Host pair " << i << " connected successfully..." << endl;
	}

	cout << "Connecting Switches with each other..." << endl;
	for(int i=0;i<num_hosts-1;i++) {
		add_new_connection(switches[i+1].port, switches[i].pid);
		usleep(SLEEP_TIME);
	}
	cout << "Topology completed..." << endl << endl;

	cout << "Adding flows from first host to every other host in the network..." << endl;
	unsigned long long flow_time_usec;
	int data_sent, packets_sent = 1;
	int path[MAX_SIZE] = {hosts1[0].id, switches[0].id, hosts2[0].id};

	add_new_flow(3, path, &flow_time_usec, &data_sent);
	cout << "Added flow for host 1 of switch 0 of length 3 in " << flow_time_usec << " us" << endl;

	for(int i=1;i<num_hosts;i++) {
		unsigned long long cur_usec;
		int cur_data;

		path[i+1] = switches[i].id;
		path[i+2] = hosts1[i].id;
		usleep(SLEEP_TIME);
		add_new_flow(i+3, path, &cur_usec, &cur_data);
		cout << "Added flow for host 1 of switch " << i << " of length " << i+3 << " in " << cur_usec << " us" << endl;

		flow_time_usec += cur_usec;
		data_sent += cur_data;
		packets_sent += (i+1);

		path[i+2] = hosts2[i].id;
		usleep(SLEEP_TIME);
		add_new_flow(i+3, path, &cur_usec, &cur_data);
		cout << "Added flow for host 2 of switch " << i << " of length " << i+3 << " in " << cur_usec << " us" << endl;
		flow_time_usec += cur_usec;
		data_sent += cur_data;
		packets_sent += (i+1);
	}

	cout << endl << "Network ready to be used..." << endl << endl;

	cout << "Sending random messages from host 1 of switch 1 to every other host in the network..." << endl;
	send_random_msg(0, &sent_time[0].sec, &sent_time[0].usec, hosts1[0].pid);
	cout << "Random message sent to host 2 of switch 1 at time "
			<< sent_time[0].sec << " s and " << sent_time[0].usec << " us" << endl;
	usleep(SLEEP_TIME);

	for(int flow_ids=1;flow_ids<(2*num_hosts-1);flow_ids+=2) {
		send_random_msg(flow_ids, &sent_time[flow_ids].sec, &sent_time[flow_ids].usec, hosts1[0].pid);
		usleep(SLEEP_TIME);

		send_random_msg(flow_ids + 1, &sent_time[flow_ids + 1].sec, &sent_time[flow_ids + 1].usec, hosts1[0].pid);
		usleep(SLEEP_TIME);

		cout << "Random message sent to host pair of switch " << flow_ids/2+1 << endl;
	}

	sleep(2);

	double total_msg_time = 0;
	for(int i=0;i<num_hosts;i++)
		total_msg_time += all_msg_times[i];

	cout << endl << "Network statistics:" << endl;
	cout << "Total number of flows: " << 2*num_hosts - 1 << endl;
	cout << "Total time to add " << (2*num_hosts-1) << " flows: " << flow_time_usec << " us" << endl;
	cout << "Total network data sent to add all flows: " << data_sent << " bytes" << endl;
	cout << "Total number of network packets sent to add all flows: " << packets_sent << endl;
	cout << "Total message propagation time: " << total_msg_time << " us" << endl << endl;

	cout << "Average time to add a single flow: " << (double)flow_time_usec/(double)(2*num_hosts-1) << "us" << endl;
	cout << "Average network data sent to add a single flow: " << data_sent/(double)(2*num_hosts-1) << " bytes" << endl;
	cout << "Average number of network packets sent to add a single flow: " << packets_sent/(double)(2*num_hosts-1) << endl;
	cout << "Average time for message propagation: " << total_msg_time / (double)(2*num_hosts-1) << " us" << endl;
	cout << "Average flow length: " << (num_hosts*num_hosts + 5*num_hosts - 3)/(2*num_hosts - 1) << endl;

	return 0;
}
