simulator: simulator.cpp sdn_controller sdn_switch sdn_host
	g++ simulator.cpp -o simulator
sdn_controller: sdn_controller.cpp
	g++ -pthread sdn_controller.cpp -o sdn_controller
sdn_switch: sdn_switch.cpp
	g++ -pthread sdn_switch.cpp -o sdn_switch
sdn_host: sdn_host.cpp
	g++ -pthread sdn_host.cpp -o sdn_host
