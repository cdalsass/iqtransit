package com.iqtransit.geo;

public class Algorithms {
	// return distance between two locations
    // measured in statute miles
    // based on code from http://introcs.cs.princeton.edu/java/44st/Location.java.html
    public static double distanceTo(double p1lat1, double p1long1, double p2lat2, double p2long2) {

        double STATUTE_MILES_PER_NAUTICALMILE = 1.15077945;
        
        double lat1 = Math.toRadians(p1lat1);
        
        double lon1 = Math.toRadians(p1long1);
        
        double lat2 = Math.toRadians(p2lat2);

        double lon2 = Math.toRadians(p2long2);

        // great circle distance in radians. using law of cosines formula
        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
        
        // each degree on a great circle of Earth is 60 nautical miles
        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICALMILE * nauticalMiles;
        
        return statuteMiles;

    }
}



