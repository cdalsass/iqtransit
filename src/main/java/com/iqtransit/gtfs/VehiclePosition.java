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
  `created` datetime DEFAULT NULL,
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

    public ArrayList<RealtimeEntity> parse(byte [] bytes) {

        ArrayList<RealtimeEntity> results = new ArrayList<RealtimeEntity>();

        CodedInputStream in = CodedInputStream.newInstance(bytes);
        FeedMessage.Builder b = FeedMessage.newBuilder();
        try {
            b.mergeFrom(in, null);
        } catch (IOException e) {
            System.out.println("Error parsing GTFS realtime data");
        }

        FeedMessage feed = b.build();
        List<FeedEntity>  entities = feed.getEntityList();
        for (  FeedEntity entity : entities) {
            
            if (!entity.hasVehicle()) {
              //continue;
            }
            System.out.println(" vehicle = " + entity.toString());
            com.iqtransit.gtfs.GtfsRealtime.VehiclePosition vehicle = entity.getVehicle();
            //Position.Builder position = Position.newBuilder();
            com.iqtransit.gtfs.GtfsRealtime.Position position = vehicle.getPosition();
            com.iqtransit.gtfs.GtfsRealtime.TripDescriptor trip = vehicle.getTrip();
            com.iqtransit.gtfs.GtfsRealtime.VehicleDescriptor vehicle_for_id = vehicle.getVehicle();
            VehiclePosition vp = new VehiclePosition(vehicle_for_id.getId(), trip.getTripId(), trip.getRouteId(), position.getLatitude(), position.getLongitude(),position.getSpeed(),position.getBearing());
            results.add(vp);

        }

        return results; 
    }


}