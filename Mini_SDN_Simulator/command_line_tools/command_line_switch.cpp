#include <iostream>
#include "../interfaces/switch_interface.h"

using namespace std;

int main() {
    cout << "Enter type of the switch (0 for normal, 1 for secure):";
    int ch, switch_pid, switch_port;
    cin >> ch;
    switch_pid = start_switch(ch, &switch_port);
    cout << "Switch accepting connections at port " << switch_port << endl;
    cout << "Menu" << endl;
    cout << "Press 1 to connect to controller" << endl;
	cout << "Press 2 to add new connection" << endl;
	cout << "Press 3 to dump switch state" << endl;
	cout << "Press 4 to exit" << endl;
	while(1) {
		cout << "Enter your choice:";
		cin >> ch;
		switch(ch) {
		case 1:
			int controller_port;
			cout << "Enter controller port:" << endl;
			cin >> controller_port;
			connect_switch_to_controller(controller_port, switch_pid);
			break;
		case 2:
			int dstn_port;
			cout << "Enter destination port:" << endl;
			cin >> dstn_port;
			add_new_connection(dstn_port, switch_pid);
			break;
		case 3:
			dump_switch_state(switch_pid);
			cout << "Switch state dumped in switch_log_" << switch_pid << ".txt file" << endl;
			break;
		case 4:
			cout << "shutting down switch..." << endl;
			exit_switch(switch_pid);
			return 0;
		default:
			cout << "Invalid choice!" << endl;
			break;
		}
	}
}
