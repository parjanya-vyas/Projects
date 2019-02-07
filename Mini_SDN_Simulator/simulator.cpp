#include <iostream>
#include <stdio.h>
#include <stdlib.h>

#define MAX_SIZE 1024

using namespace std;

int controller_count = 0, switch_count = 0, host_count = 0;

int main() {
	int choice;

	cout << "Starting Simulation..." << endl;
	while(1) {
		cout << "Menu:" << endl;
		cout << "1. Create Controller" << endl;
		cout << "2. Create Switch" << endl;
		cout << "3. Create Host" << endl;
		cout << "4. Exit" << endl;
		cout << "Enter Choice: ";
		cin >> choice;

		switch(choice) {
			case 1: {
				int ch;
				char cmd[MAX_SIZE];
				cout << "Press 0 for a normal or 1 for a secure controller" << endl;
				cin >> ch;
				sprintf(cmd, "gnome-terminal -x ./sdn_controller %d", ch);
				system(cmd);
				break;
			}
			case 2: {
				int ch;
				char cmd[MAX_SIZE];
				cout << "Press 0 for a normal or 1 for a secure switch" << endl;
				cin >> ch;
				sprintf(cmd, "gnome-terminal -x ./sdn_switch %d", ch);
				system(cmd);
				break;
			}
			case 3: {
				int ch;
				char cmd[MAX_SIZE];
				sprintf(cmd, "gnome-terminal -x ./sdn_host");
				system(cmd);
				break;
			}
			case 4: {
				return 0;
			}
			default: {
				cout << "Invalid choice!" << endl;
				break;
			}
		}
	}
	
	return 0;
}
