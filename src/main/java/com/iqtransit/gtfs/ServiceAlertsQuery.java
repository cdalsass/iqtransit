package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeQuery;
import com.iqtransit.agency.AgencyInterface;
import java.util.ArrayList;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import com.iqtransit.gtfs.ServiceAlert;
import java.io.IOException;
import java.util.List;

/* responsible for parsing and downloading from remote source. */

public class ServiceAlertsQuery extends RealtimeQuery {

	public ServiceAlertsQuery(AgencyInterface agency) {
			super(agency);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.ServiceAlertUrl();
	}

	public ArrayList<RealtimeResult> parse() {

		ArrayList<RealtimeResult> results = new ArrayList<RealtimeResult>();

        CodedInputStream in = CodedInputStream.newInstance(this.last_prediction.bytes);
        FeedMessage.Builder b = FeedMessage.newBuilder();
        try {
        	b.mergeFrom(in, null);
        } catch (IOException e) {
        	// need better way to handle these errors in future.
        	System.out.println("Error parsing GTFS realtime data");
        }

        FeedMessage feed = b.build();
        List<FeedEntity>  entities = feed.getEntityList();
        for (  FeedEntity entity : entities) {
        	
        	if (!entity.hasVehicle()) {
		      //continue;
		    }
		    
		   
		    com.iqtransit.gtfs.GtfsRealtime.Alert alert = entity.getAlert();

		    java.util.List<com.iqtransit.gtfs.GtfsRealtime.EntitySelector> informed_entity_list = alert.getInformedEntityList();
		    // com.iqtransit.gtfs.GtfsRealtime.ActivePeriod ap = alert.getActivePeriod();
		    java.util.List<com.iqtransit.gtfs.GtfsRealtime.TimeRange> active_period_list = alert.getActivePeriodList();

		    //Position.Builder position = Position.newBuilder();
		  //  com.iqtransit.gtfs.GtfsRealtime.Position position = vehicle.getPosition();
		   // com.iqtransit.gtfs.GtfsRealtime.TripDescriptor trip = vehicle.getTrip();
		  //  com.iqtransit.gtfs.GtfsRealtime.VehicleDescriptor vehicle_for_id = vehicle.getVehicle();
		   // VehiclePosition vp = new VehiclePosition(vehicle_for_id.getId(), trip.getTripId(), position.getLatitude(), position.getLongitude(),position.getSpeed(),position.getBearing());

		    String description_text = null;
		    String header_text = null;

		    if (alert.hasDescriptionText()) {
		    	description_text = alert.getDescriptionText().getTranslation(0).getText();
		    }
		    if (alert.hasHeaderText()) {
		    	header_text = alert.getHeaderText().getTranslation(0).getText();
		    }

		    com.iqtransit.gtfs.ServiceAlert service_alert = new com.iqtransit.gtfs.ServiceAlert(entity.getId(), alert.getCause().getNumber(), alert.getEffect().getNumber(), header_text, description_text);
		    results.add(service_alert);

        }

        this.last_prediction.was_parsed = true; 
        return results; 
	}
}