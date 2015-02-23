package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeResult;
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

public class TripUpdate extends RealtimeEntity {

	public String id;
    public String trip_id;
    public Integer schedule_relationship;
    public String route_id;
    public String stop_id;
    public int stop_sequence;
    public String arrival_or_departure;
    public Integer delay;
    public String vehicle_id;
    public String vehicle_label;

    public TripUpdate(String id, String trip_id, Integer schedule_relationship, String route_id, String stop_id, Integer stop_sequence, String arrival_or_departure, Integer delay ) {

        this.id = id;
        this.trip_id = trip_id;
        this.schedule_relationship = schedule_relationship;
        this.route_id = route_id;
        this.stop_id = stop_id;
        this.stop_sequence = stop_sequence;
        this.arrival_or_departure = arrival_or_departure;
        this.delay = delay;
    }
	
	public String toString() {
        return "id = " + id + " trip_id = " + trip_id + " schedule_relationship = " + schedule_relationship + " route_id = " + route_id + " stop_id = " + stop_id + " stop_sequence = " + stop_sequence + " arrival_or_departure = " + arrival_or_departure.toString() + " delay = " + delay +  " vehicle_id = " + vehicle_id + " vehicle_label = " + vehicle_label;
    }
    
    public boolean store(Connection conn) throws SQLException {

        String query = "INSERT INTO trip_update (id, created, external_id, trip_id, schedule_relationship, route_id, stop_id, stop_sequence, arrival_or_departure, delay) values (null, now(), ?,?,?,?,?,?,?,?)";
 
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString (1, this.id);
        preparedStmt.setString (2, this.trip_id);
        preparedStmt.setInt (3, this.schedule_relationship);
        preparedStmt.setString (4, this.route_id);
        preparedStmt.setString (5, this.stop_id);

        preparedStmt.setInt (5, this.stop_sequence);
        preparedStmt.setString (5, this.arrival_or_departure);
        preparedStmt.setInt (5, this.delay);

        preparedStmt.execute();
        //System.out.println("tripid = " + l.id + " bearing = " + l.bearing + " speed = " + l.speed + " latitude = " + l.latitude + " longitude" + l.longitude);
        return true; 
    }

    

}