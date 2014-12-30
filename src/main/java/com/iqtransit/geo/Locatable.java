package com.iqtransit.geo;

public class Locatable {
	private double latitude;
	private double longitude;
	private String id;

	public Locatable(String id, double latitute, double longitude) {
		this.latitude = latitute;
		this.longitude = longitude;
		this.id = id;
	}

	public double getLatitute() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getId() {
		return id;
	}

	public String toString() {
        return id + " (" + latitude + ", " + longitude + ")";
    }

    // return distance between two locations
    // measured in statute miles
    // based on code from http://introcs.cs.princeton.edu/java/44st/Location.java.html
    public double distanceTo(Locatable that) {
        double STATUTE_MILES_PER_NAUTICALMILE = 1.15077945;
        
        double lat1 = Math.toRadians(this.latitude);
        
        double lon1 = Math.toRadians(this.longitude);
        
        double lat2 = Math.toRadians(that.latitude);

        double lon2 = Math.toRadians(that.longitude);

        // great circle distance in radians. using law of cosines formula
        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
        
        // each degree on a great circle of Earth is 60 nautical miles
        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICALMILE * nauticalMiles;
        return statuteMiles;
    }
}