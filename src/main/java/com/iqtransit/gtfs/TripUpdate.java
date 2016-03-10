package com.iqtransit.gtfs;
import com.iqtransit.common.RealtimeResult;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.Types; 

import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import com.iqtransit.gtfs.ServiceAlert;
import java.io.IOException;
import java.util.List;
import com.iqtransit.common.*;


/* 
   CREATE TABLE `trip_update` (
  `id` int(11) NOT NULL auto_increment,
  `created` datetime DEFAULT NULL,
  `external_id`  varchar(255) NOT NULL,
  `trip_id` varchar(255) NOT NULL,
  `schedule_relationship` int(11)  null,
   route_id varchar(255) not null,
   stop_id varchar(255) not null,
   stop_sequence int(11) not null,
   arrival_or_departure char(1),
   delay int(11),
   arrive_or_depart_time BIGINT,
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx23453` (`id`)
);


trip_update (id, created, external_id, trip_id, schedule_relationship, route_id, stop_id, stop_sequence, arrival_or_departure, delay, arrive_or_depart_time) values (null, now(), ?,?,?,?,?,?,?,?,?)
*/


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
    public Long arrive_or_depart_time; 

    public TripUpdate(String id, String trip_id, Integer schedule_relationship, String route_id, String stop_id, Integer stop_sequence, String arrival_or_departure, Integer delay, Long arrive_or_depart_time ) {

        this.id = id;
        this.trip_id = trip_id;
        this.schedule_relationship = schedule_relationship;
        this.route_id = route_id;
        this.stop_id = stop_id;
        this.stop_sequence = stop_sequence;
        this.arrival_or_departure = arrival_or_departure;
        this.delay = delay;
        this.arrive_or_depart_time = arrive_or_depart_time;
    }
	
	public String toString() {
        return "id = " + id + " trip_id = " + trip_id + " schedule_relationship = " + schedule_relationship + " route_id = " + route_id + " stop_id = " + stop_id + " stop_sequence = " + stop_sequence + " arrival_or_departure = " + arrival_or_departure.toString() + " delay = " + delay +  " vehicle_id = " + vehicle_id + " vehicle_label = " + vehicle_label + " arrive_or_depart_time " + arrive_or_depart_time;
    }
    
    public boolean store(Connection conn) throws SQLException {

        String query = "INSERT INTO trip_update (id, created, external_id, trip_id, schedule_relationship, route_id, stop_id, stop_sequence, arrival_or_departure, delay, arrive_or_depart_time) values (null, now(), ?,?,?,?,?,?,?,?,?)";
 
        PreparedStatement preparedStmt = conn.prepareStatement(query);

        if (this.id != null) {
            preparedStmt.setString (1, this.id);
        } else {
            preparedStmt.setNull(1,Types.NULL);
        }


        if (this.trip_id != null) {
            preparedStmt.setString (2, this.trip_id);
        } else {
            preparedStmt.setNull(2,Types.NULL);
        }

        if (this.schedule_relationship != null) {
            preparedStmt.setInt (3, this.schedule_relationship);
        } else {
            preparedStmt.setNull(3,Types.NULL);
        }
       
        if (this.route_id != null) {
            preparedStmt.setString (4, this.route_id);
        } else {
            preparedStmt.setNull(4,Types.NULL);
        }

        if (this.stop_id != null) {
            preparedStmt.setString (5, this.stop_id);
        } else {
            preparedStmt.setNull(5,Types.NULL);
        }

        preparedStmt.setInt (6, this.stop_sequence);
        preparedStmt.setString (7, this.arrival_or_departure);

        if (this.delay != null) {
            preparedStmt.setInt (8, this.delay);
        } else {
            preparedStmt.setNull(8,Types.NULL);
        }

        if (this.arrive_or_depart_time != null) {
            preparedStmt.setLong (9, this.arrive_or_depart_time);
        } else {
            preparedStmt.setNull(9,Types.NULL);
        }


        preparedStmt.execute();

        return true; 
    }

    

}