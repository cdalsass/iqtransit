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

    public VehiclePosition(String id, String trip, double latitute, double longitude, double speed, double bearing) {
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
    

    public boolean store(Connection conn) throws SQLException {

        String query = "INSERT INTO vehicle_position (id, created, vehicle_id, speed, bearing, longitude, latitude) values (null, now(), ?,?,?,?,?)";

        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString (1, this.id);
        preparedStmt.setDouble (2, this.speed);
        preparedStmt.setDouble (3, this.bearing);
        preparedStmt.setDouble (4, this.longitude);
        preparedStmt.setDouble (5, this.latitude);
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
            VehiclePosition vp = new VehiclePosition(vehicle_for_id.getId(), trip.getTripId(), position.getLatitude(), position.getLongitude(),position.getSpeed(),position.getBearing());
            results.add(vp);

        }

        return results; 
    }


}