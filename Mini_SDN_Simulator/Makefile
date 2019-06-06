simulator: command_line_controller command_line_switch command_line_host ;

command_line_controller: ./command_line_tools/command_line_controller.cpp sdn_controller
	g++ -std=c++11 ./command_line_tools/command_line_controller.cpp -o ./bin/command_line_controller
command_line_switch: ./command_line_tools/command_line_switch.cpp sdn_switch
	g++ -std=c++11 ./command_line_tools/command_line_switch.cpp -o ./bin/command_line_switch
command_line_host: ./command_line_tools/command_line_host.cpp sdn_host
	g++ -pthread -std=c++11 ./command_line_tools/command_line_host.cpp -o ./bin/command_line_host

experiments: ./experiments/topology_1.cpp sdn_controller sdn_switch sdn_host
	g++ -pthread -std=c++11 ./experiments/topology_1.cpp -o ./bin/topology_1
	g++ -pthread -std=c++11 ./experiments/topology_2.cpp -o ./bin/topology_2
	g++ -pthread -std=c++11 ./experiments/topology_3.cpp -o ./bin/topology_3

sdn_controller: ./core_modules/sdn_controller.cpp ./sha256/sha256.cpp
	g++ -pthread -std=c++11 ./sha256/sha256.cpp ./core_modules/sdn_controller.cpp -o ./bin/sdn_controller
sdn_switch: ./core_modules/sdn_switch.cpp ./sha256/sha256.cpp
	g++ -pthread -std=c++11 ./sha256/sha256.cpp ./core_modules/sdn_switch.cpp -o ./bin/sdn_switch
sdn_host: ./core_modules/sdn_host.cpp
	g++ -pthread -std=c++11 ./core_modules/sdn_host.cpp -o ./bin/sdn_host

clean:
	pkill sdn || true
	rm -f ./bin/*
	rm -f ./logs/*
	rm -f ./temp/*
