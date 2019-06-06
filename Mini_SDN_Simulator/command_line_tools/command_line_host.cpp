#include <iostream>
#include "../interfaces/host_interface.h"

using namespace std;

void message_handler(int pid, char *msg, unsigned long long recv_time_sec, unsigned long long recv_time_usec) {
	cout << "Message received:" << msg << endl;
	cout << "Receiving time: " << recv_time_sec << " sec " << recv_time_usec << "usec" << endl;
}

int main() {
    int ch, host_pid;
    host_pid = start_host();
    cout << "Menu" << endl;
    cout << "1. Connect to controller" << endl;
	cout << "2. Connect to switch" << endl;
	cout << "3. Send new random message" << endl;
	cout << "4. Exit" << endl;
	while(1) {
		cout << "Enter your choice:";
		cin >> ch;
		switch(ch) {
		case 1:
			int controller_port;
			cout << "Enter controller port:" << endl;
			cin >> controller_port;
			connect_host_to_controller(controller_port, host_pid);
			break;
		case 2:
			int switch_port;
			cout << "Enter switch port:" << endl;
			cin >> switch_port;
			connect_host_to_switch(switch_port, host_pid);
			break;
		case 3:
			int flow_id;
			unsigned long long sent_time_sec, sent_time_usec;
			cout << "Enter flow id:" << endl;
			cin >> flow_id;
			send_random_msg(flow_id, &sent_time_sec, &sent_time_usec, host_pid);
			cout << "Message sent at time " << sent_time_sec << " sec " << sent_time_usec << " usec" << endl;
			break;
		case 4:
			cout << "shutting down host..." << endl;
			exit_host(host_pid);
			return 0;
		default:
			cout << "Invalid choice!" << endl;
			break;
		}
	}
}
