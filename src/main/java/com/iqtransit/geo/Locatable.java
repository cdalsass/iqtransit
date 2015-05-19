package com.iqtransit.geo;
import com.iqtransit.gtfs.VehiclePosition;

public class Locatable {
	public double latitude;
	public double longitude;
    public double bearing;
    public double speed;
	public String id;
    public String trip;

	public Locatable(String id, double latitute, double longitude) {
		this.latitude = latitute;
		this.longitude = longitude;
		this.id = id;
	}

    public Locatable(String id, String trip, double latitute, double longitude, double speed, double bearing) {
        this.latitude = latitute;
        this.longitude = longitude;
        this.speed = speed;
        this.bearing = bearing; 
        this.id = id;
        this.trip = trip;
    }
	
	public String toString() {
        return id + " (" + latitude + ", " + longitude + ","  + speed + "," + bearing + ")";
    }

    
}