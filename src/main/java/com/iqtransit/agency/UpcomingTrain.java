package com.iqtransit.agency;

public class UpcomingTrain {
	
	public String identifier;
	public String short_name;
	public String arrival_time;
	public String stop_name;
	public String stop_id; 
	public String start_time;
	public String terminal_time; 
	public String start_stop_id;
	public String terminal_stop_id;
	public String terminal_stop_name;
	public String route_id;
	public String route_name;


	public UpcomingTrain(String identifier, String short_name, String arrival_time, String stop_name, String stop_id, String start_time, String terminal_time, String start_stop_id, String terminal_stop_id, String terminal_stop_name, String route_id, String route_name) {

		this.identifier = identifier;
		this.short_name = short_name;
		this.arrival_time = arrival_time;
		this.stop_name = stop_name;
		this.stop_id = stop_id;
		this.start_time = start_time;
		this.terminal_time = terminal_time;
		this.start_stop_id = start_stop_id;
		this.terminal_stop_id = terminal_stop_id;
		this.terminal_stop_name = terminal_stop_name;
		this.route_id = route_id;
		this.route_name = route_name;

	}

}