package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeQuery;
import com.iqtransit.agency.AgencyInterface;
import java.util.ArrayList;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import java.io.IOException;
import java.util.List;

/* responsible for parsing and downloading from remote source. */

public class TripUpdatesQuery extends RealtimeQuery {

	public TripUpdatesQuery(AgencyInterface agency) {
			super(agency);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.TripUpdatesUrl();
	}


	public ArrayList<RealtimeResult> parse() {

		ArrayList<RealtimeResult> results = new ArrayList<RealtimeResult>();

        CodedInputStream in = CodedInputStream.newInstance(this.last_prediction.bytes);
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

        this.last_prediction.was_parsed = true; 
        return results; 
	}
}