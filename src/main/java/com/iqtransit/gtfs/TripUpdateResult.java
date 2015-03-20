package com.iqtransit.gtfs;
import com.iqtransit.common.RealtimeResult;
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
import com.iqtransit.gtfs.TripUpdate;
import java.io.IOException;
import java.util.List;
import com.iqtransit.common.*;

public class TripUpdateResult extends RealtimeResult {


    public TripUpdateResult(RealtimeSource rts) {
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

        for (FeedEntity entity : entities ) {
        
            com.iqtransit.gtfs.GtfsRealtime.TripUpdate trip_update = entity.getTripUpdate();
          
            if (trip_update.hasTrip()) {

                TripDescriptor trip = trip_update.getTrip();
                Integer schedule_relationship = null;

                if (trip.hasScheduleRelationship()) {
                    schedule_relationship = trip.getScheduleRelationship().getNumber();
                } 

                String route_id = null;
                if (trip.hasRouteId()) {
                    route_id = trip.getRouteId();
                }

                /*if (trip.hasStartDate()) {
                    stmt.setString(7, trip.getStartDate());
                }  else {
                    stmt.setNull(7, Types.VARCHAR);
                }

                if (trip.hasStartTime()) {
                    stmt.setString(8, trip.getStartTime());
                } else {
                    stmt.setNull(8, Types.VARCHAR);
                }*/

                String trip_id = null;
                if (trip.hasTripId()) {
                    trip_id = trip.getTripId();
                } 

                for (com.iqtransit.gtfs.GtfsRealtime.TripUpdate.StopTimeUpdate stu : trip_update.getStopTimeUpdateList()) {

                    String stop_id = null;
                    if (stu.hasStopId()) {
                        stop_id = stu.getStopId();
                    }   

                    int stop_sequence = stu.hasStopSequence() ? stu.getStopSequence() : -1;

                    String arrival_or_departure = null;
                    Integer delay = null;
                    Long arrive_or_depart_time = null;

                    if (stu.hasArrival()) {
                        com.iqtransit.gtfs.GtfsRealtime.TripUpdate.StopTimeEvent ste = stu.getArrival();
                        arrival_or_departure = "A";

                        
                        if (ste.hasDelay()) {
                            delay = ste.getDelay();
                        }

                        if (ste.hasTime()) {
                            arrive_or_depart_time = ste.getTime();
                        }

                    }
                    

                    if (stu.hasDeparture()) {
                        com.iqtransit.gtfs.GtfsRealtime.TripUpdate.StopTimeEvent ste = stu.getDeparture();
                        arrival_or_departure = "D";

                        if (ste.hasDelay()) {
                            delay = ste.getDelay();
                        }


                        if (ste.hasTime()) {
                            arrive_or_depart_time = ste.getTime();
                        }

                    }

                    TripUpdate new_update = new TripUpdate(entity.getId(), trip_id, schedule_relationship,route_id, stop_id, stop_sequence ,arrival_or_departure, delay, arrive_or_depart_time);

                    results.add(new_update);

                }
            }

        }

        return results; 
    }

}