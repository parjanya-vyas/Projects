#include <iostream>

#include "../interfaces/controller_interface.h"
#include "../interfaces/switch_interface.h"
#include "../interfaces/host_interface.h"

#define SLEEP_TIME 20000

using namespace std;

typedef struct time_sec_usec {
	unsigned long long sec, usec;
} TIME;

int secure_sdn, num_hosts;
int controller_port;
int switch_pid, switch_port, switch_id;
int host_pid[MAX_SIZE], host_start_id;

TIME sent_time[MAX_SIZE];
unsigned long long all_msg_times[MAX_SIZE];

int get_host_index_from_pid_for_recv_time(int pid) {
	for(int i=0;i<num_hosts;i++) {
		if(host_pid[i] == pid)
			return i;
	}

	return -1;
}

void message_handler(int pid, char *msg, unsigned long long recv_time_sec, unsigned long long recv_time_usec) {
	int host_index = get_host_index_from_pid_for_recv_time(pid) - 1;
	if(host_index < 0) {
		cout << "Invalid pid received!" << endl;
		return;
	}

	all_msg_times[host_index] = 1000000 * (recv_time_sec - sent_time[host_index].sec)
			+ (recv_time_usec - sent_time[host_index].usec);
	cout << "Message propagation time for flow " << host_index << " " << all_msg_times[host_index] << "usec" << endl;
}

int main(int argc, char *argv[]) {
	if(argc != 3) {
		cout << "Incorrect arguments!" << endl;
		cout << "Proper usage: ./topology_1 <secure bit> <number of hosts>" << endl;

		return 0;
	}

	secure_sdn = strtol(argv[1], NULL, 10);
	num_hosts = strtol(argv[2], NULL, 10);

	controller_port = start_controller(secure_sdn);

	switch_pid = start_switch(secure_sdn, &switch_port);
	connect_switch_to_controller(controller_port, switch_pid);
	switch_id = 0;

	host_start_id = 1;
	cout << "Starting " << num_hosts << " hosts and connecting them to the switch..." << endl;
	for(int i=0;i<num_hosts;i++) {
		host_pid[i] = start_host();
		connect_host_to_controller(controller_port, host_pid[i]);
		usleep(SLEEP_TIME);
		connect_host_to_switch(switch_port, host_pid[i]);
		usleep(SLEEP_TIME);
	}

	cout << "Topology completed..." << endl << endl;

	cout << "Adding flows from host 1 to every other host..." << endl;
	unsigned long long flow_time_usec=0;
	int data_sent=0;
	for(int i=1;i<num_hosts;i++) {
		unsigned long long cur_usec;
		int cur_data;
		int path[] = {host_start_id, switch_id, host_start_id+i};
		add_new_flow(3, path, &cur_usec, &cur_data);
		cout << "Added flow " << i << " of length 3 in " << cur_usec << "microseconds" << endl;
		usleep(SLEEP_TIME);
		flow_time_usec += cur_usec;
		data_sent += cur_data;
	}

	cout << endl << "Network ready to be used..." << endl << endl;

	cout << "Sending random messages from host 1 to all other hosts in the network" << endl;
	for(int i=0;i<num_hosts-1;i++) {
		send_random_msg(i, &sent_time[i].sec, &sent_time[i].usec, host_pid[0]);
		cout << "Random message sent to host " << i+2 << " at time "
				<< sent_time[i].sec << " s and " << sent_time[i].usec << " us" << endl;
		usleep(SLEEP_TIME);
	}

	sleep(2);

	double total_msg_time = 0;
	for(int i=0;i<num_hosts;i++)
		total_msg_time += all_msg_times[i];

	cout << endl << "Network statistics:" << endl;
	cout << "Total number of flows: " << num_hosts-1 << endl;
	cout << "Total time to add " << num_hosts-1 << " flows of length 3: " << flow_time_usec << " us" << endl;
	cout << "Total network data sent to add all flows: " << data_sent << " bytes" << endl;
	cout << "Total number of network packets sent to add all flows: " << num_hosts-1 << endl;
	cout << "Total message propagation time: " << total_msg_time << " us" << endl << endl;

	cout << "Average time to add a single flow of length 3: " << (double)flow_time_usec/(double)(num_hosts-1) << "us" << endl;
	cout << "Average network data sent to add a single flow of length 3: " << data_sent/(num_hosts-1) << " bytes" << endl;
	cout << "Average number of network packets sent to add a single flow of length 3: 1" << endl;
	cout << "Average time for message propagation: " << total_msg_time / (double)(num_hosts-1) << " us" << endl;
	cout << "Average flow length: 3" << endl;

	return 0;
}
