package com.iqtransit.gtfs;



// simpler, non-dependent version of GTFSRT EntitySelector class.

public class Entity {
	public String agency_id;
	public String route_id;
	public int route_type;

	public void Entity() {
		agency_id = null;
		route_id = null;
		route_type = 0;
	}	

	public void Entity(String agency_id, String route_id, int route_type) {
		this.agency_id = agency_id;
		this.route_id = route_id;
		this.route_type = route_type;
	}

}