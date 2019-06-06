#include <iostream>
#include "../interfaces/controller_interface.h"

using namespace std;

int main() {
    cout << "Enter type of the controller (0 for normal, 1 for secure):";
    int ch, controller_port;
    cin >> ch;
    controller_port = start_controller(ch);
    cout << "Controller listening on port " << controller_port << endl;
    cout << "Menu" << endl;
	cout << "1. Add new flow" << endl;
	cout << "2. Dump controller state" << endl;
	cout << "3. Exit" << endl;
	while(1) {
		cout << "Enter your choice:";
		cin >> ch;
		switch(ch) {
		case 1:
			int path_len, *path, data_sent;
			unsigned long long time_taken;
			cout << "Enter Path length:";
			cin >> path_len;
			path = (int *)malloc(path_len * sizeof(int));
			cout << "Enter path (space separated):" << endl;
			for(int i=0;i<path_len;i++)
				cin >> path[i];
			add_new_flow(path_len, path, &time_taken, &data_sent);
			cout << "Time taken to add this flow: " << time_taken << endl;
			cout << "Data sent to add this flow: " << data_sent << endl;
			break;
		case 2:
			dump_state();
			cout << "State dumped in log file named controller_log.txt" << endl;
			break;
		case 3:
			exit_controller();
			return 0;
		default:
			cout << "Invalid choice!" << endl;
			break;
		}
	}
}
