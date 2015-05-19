package com.iqtransit.gtfs;
import com.iqtransit.common.*;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;


import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import com.iqtransit.gtfs.ServiceAlert;
import java.io.IOException;
import java.util.List;

public class VehiclePosition extends RealtimeEntity {

	public double latitude;
	public double longitude;
  public double bearing;
  public double speed;
	public String id;
  public String trip;
  public String route_id;

    public VehiclePosition(String id, String trip, String route_id, double latitute, double longitude, double speed, double bearing) {
        this.latitude = latitute;
        this.longitude = longitude;
        this.speed = speed;
        this.bearing = bearing; 
        this.id = id;
        this.trip = trip;
        this.route_id = route_id;
    }
	
	public String toString() {
        return id + " (" + latitude + ", " + longitude + ","  + speed + "," + bearing + ")";
    }

    /*
     CREATE TABLE `vehicle_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp ,
  `vehicle_id` varchar(500) NOT NULL,
  `trip_id` varchar(255) not null,
  `route_id` varchar(500) not null,
  `speed` double null,
  `bearing` double NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  PRIMARY KEY (`id`)
) */
    

    public boolean store(Connection conn) throws SQLException {

        String query = "INSERT INTO vehicle_position (id, created, vehicle_id, trip_id, route_id, `speed`, bearing, longitude, latitude) values (null, now(), ?,?,?,?,?,?,?)";

        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString (1, this.id);
        preparedStmt.setString (2, this.trip);
        preparedStmt.setString (3, this.route_id);
        preparedStmt.setDouble (4, this.speed);
        preparedStmt.setDouble (5, this.bearing);
        preparedStmt.setDouble (6, this.longitude);
        preparedStmt.setDouble (7, this.latitude);
        preparedStmt.execute();
        //System.out.println("tripid = " + l.id + " bearing = " + l.bearing + " speed = " + l.speed + " latitude = " + l.latitude + " longitude" + l.longitude);
        return true; 
    }


    // return distance between two locations
    // measured in statute miles
    // based on code from http://introcs.cs.princeton.edu/java/44st/Location.java.html
    public double distanceTo(VehiclePosition that) {
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