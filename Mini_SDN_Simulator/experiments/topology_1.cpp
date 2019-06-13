#include <iostream>

#include "../interfaces/controller_interface.h"
#include "../interfaces/switch_interface.h"
#include "../interfaces/host_interface.h"

#define SLEEP_TIME 50000

using namespace std;

typedef struct time_sec_usec {
	unsigned long long sec, usec;
} TIME;

int secure_sdn, num_hosts;
int controller_port;
int switch1_pid, switch1_port, switch1_id;
int switch2_pid, switch2_port, switch2_id;
int host1_pid[MAX_SIZE], host2_pid[MAX_SIZE], host1_start_id, host2_start_id;

TIME sent_time[MAX_SIZE];
unsigned long long all_msg_times[MAX_SIZE];

int get_host_index_from_pid_for_recv_time(int pid) {
	for(int i=0;i<num_hosts;i++) {
		if(host2_pid[i] == pid)
			return i;
	}

	return -1;
}

void message_handler(int pid, char *msg, unsigned long long recv_time_sec, unsigned long long recv_time_usec) {
	int host_index = get_host_index_from_pid_for_recv_time(pid);
	if(host_index == -1) {
		cout << "Invalid pid received!" << endl;
		return;
	}

	all_msg_times[host_index] = 1000000 * (recv_time_sec - sent_time[host_index].sec)
			+ (recv_time_usec - sent_time[host_index].usec);
	cout << "Message propagation time for message " << host_index << " " << all_msg_times[host_index] << "usec" << endl;
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

	switch1_pid = start_switch(secure_sdn, &switch1_port);
	connect_switch_to_controller(controller_port, switch1_pid);
	switch1_id = 0;
	usleep(SLEEP_TIME);

	switch2_pid = start_switch(secure_sdn, &switch2_port);
	connect_switch_to_controller(controller_port, switch2_pid);
	switch2_id = 1;
	usleep(SLEEP_TIME);

	host1_start_id = 2;
	cout << "Starting " << num_hosts << " hosts and connecting them to switch 1..." << endl;
	for(int i=0;i<num_hosts;i++) {
		host1_pid[i] = start_host();
		connect_host_to_controller(controller_port, host1_pid[i]);
		usleep(SLEEP_TIME);
		connect_host_to_switch(switch1_port, host1_pid[i]);
		usleep(SLEEP_TIME);
	}

	cout << "Starting another " << num_hosts << " hosts and connecting them to switch 2..." << endl;
	host2_start_id = 2+num_hosts;
	for(int i=0;i<num_hosts;i++) {
		host2_pid[i] = start_host();
		connect_host_to_controller(controller_port, host2_pid[i]);
		usleep(SLEEP_TIME);
		connect_host_to_switch(switch2_port, host2_pid[i]);
		usleep(SLEEP_TIME);
	}

	cout << "Connecting Switch 1 and 2..." << endl;
	add_new_connection(switch2_port, switch1_pid);
	usleep(SLEEP_TIME);
	cout << "Topology completed..." << endl << endl;

	cout << "Adding flows from every host with switch 1 to corresponding host with switch 2..." << endl;
	unsigned long long flow_time_usec=0;
	int data_sent=0;
	for(int i=0;i<num_hosts;i++) {
		unsigned long long cur_usec;
		int cur_data;
		int path[] = {host1_start_id+i, switch1_id, switch2_id, host2_start_id+i};
		add_new_flow(4, path, &cur_usec, &cur_data);
		cout << "Added flow " << i << " of length 4 in " << cur_usec << "microseconds" << endl;
		usleep(5*SLEEP_TIME);
		flow_time_usec += cur_usec;
		data_sent += cur_data;
	}

	cout << endl << "Network ready to be used..." << endl << endl;

	cout << "Sending random messages from every host with switch 1 to corresponding host with switch 2" << endl;
	for(int i=0;i<num_hosts;i++) {
		send_random_msg(i, &sent_time[i].sec, &sent_time[i].usec, host1_pid[i]);
		cout << "Random message sent from host " << i << " at time "
				<< sent_time[i].sec << " s and " << sent_time[i].usec << " us" << endl;
		usleep(SLEEP_TIME);
	}

	sleep(2);

	double total_msg_time = 0;
	for(int i=0;i<num_hosts;i++)
		total_msg_time += all_msg_times[i];

	cout << endl << "Network statistics:" << endl;
	cout << "Total number of flows: " << num_hosts << endl;
	cout << "Total time to add " << num_hosts << " flows of length 4: " << flow_time_usec << " us" << endl;
	cout << "Total network data sent to add all flows: " << data_sent << " bytes" << endl;
	cout << "Total number of network packets sent to add all flows: " << 2*num_hosts << endl;
	cout << "Total message propagation time: " << total_msg_time << " us" << endl << endl;

	cout << "Average time to add a single flow of length 4: " << (double)flow_time_usec/(double)num_hosts << "us" << endl;
	cout << "Average network data sent to add a single flow of length 4: " << data_sent/num_hosts << " bytes" << endl;
	cout << "Average number of network packets sent to add a single flow of length 4: " << 2 << endl;
	cout << "Average time for message propagation: " << total_msg_time / (double)num_hosts << " us" << endl;
	cout << "Average flow length: 4" << endl;

	return 0;
}
