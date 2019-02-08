simulator: simulator.cpp sdn_controller sdn_switch sdn_host
	g++ -std=c++11 simulator.cpp -o simulator
sdn_controller: sdn_controller.cpp sha256.cpp
	g++ -pthread -std=c++11 sha256.cpp sdn_controller.cpp -o sdn_controller
sdn_switch: sdn_switch.cpp sha256.cpp
	g++ -pthread -std=c++11 sha256.cpp sdn_switch.cpp -o sdn_switch
sdn_host: sdn_host.cpp
	g++ -pthread -std=c++11 sdn_host.cpp -o sdn_host
