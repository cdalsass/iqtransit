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

public class VehiclePositionResult extends RealtimeResult {

    public VehiclePositionResult(RealtimeSource rts) {
        super(rts);
    }


    public ArrayList<RealtimeEntity> parse() {

        ArrayList<RealtimeEntity> results = new ArrayList<RealtimeEntity>();

        CodedInputStream in = CodedInputStream.newInstance(this.source.getLoadedBytes());
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
           // System.out.println(" vehicle = " + entity.toString());
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