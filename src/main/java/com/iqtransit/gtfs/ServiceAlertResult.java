package com.iqtransit.gtfs;
import java.util.ArrayList;

import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import com.iqtransit.gtfs.ServiceAlert;
import java.io.IOException;
import java.util.List;

public class ServiceAlertResult extends RealtimeResult {
	

	public ServiceAlertResult(RealtimeSource rts) {
			super(rts);
	}

	public ArrayList<RealtimeEntity> parse() {

        ArrayList<RealtimeEntity> results = new ArrayList<RealtimeEntity>();

        CodedInputStream in = CodedInputStream.newInstance(this.source.getLoadedBytes());
        FeedMessage.Builder b = FeedMessage.newBuilder();
        try {
          b.mergeFrom(in, null);
        } catch (IOException e) {
          // need better way to handle these errors in future.
          System.out.println("Error parsing GTFS realtime data");
        }

        FeedMessage feed = b.build();
        List<FeedEntity>  entities = feed.getEntityList();

        for (FeedEntity entity : entities) {
          
          if (!entity.hasVehicle()) {
          //continue;
        }
        
        com.iqtransit.gtfs.GtfsRealtime.Alert alert = entity.getAlert();

        java.util.List<com.iqtransit.gtfs.GtfsRealtime.EntitySelector> informed_entity_list = alert.getInformedEntityList();
        // com.iqtransit.gtfs.GtfsRealtime.ActivePeriod ap = alert.getActivePeriod();
        java.util.List<com.iqtransit.gtfs.GtfsRealtime.TimeRange> active_period_list = alert.getActivePeriodList();

        ArrayList<com.iqtransit.gtfs.TimeRange> internal_active_period_list = new ArrayList<com.iqtransit.gtfs.TimeRange>();

        for (com.iqtransit.gtfs.GtfsRealtime.TimeRange range : active_period_list) {

          // non google version of Range 
          com.iqtransit.gtfs.TimeRange new_range = new com.iqtransit.gtfs.TimeRange();

          if (range.hasStart()) {
            new_range.start = range.getStart();
              }
              if (range.hasEnd()) {
                  new_range.end = range.getEnd();
              }

              internal_active_period_list.add(new_range);
        }

        /**** SIMILAR SECTIONS ***/

        java.util.List<com.iqtransit.gtfs.GtfsRealtime.EntitySelector> informed_entities = alert.getInformedEntityList();

        ArrayList<com.iqtransit.gtfs.Entity> my_informed_entities = new ArrayList<com.iqtransit.gtfs.Entity>();

        for (com.iqtransit.gtfs.GtfsRealtime.EntitySelector informed_entity : informed_entities) {

          // non google version of EntitySelector 
          com.iqtransit.gtfs.Entity new_informed_entity = new com.iqtransit.gtfs.Entity();

          new_informed_entity.agency_id = informed_entity.getAgencyId();
              
              if (informed_entity.hasRouteId()) {
                  new_informed_entity.route_id = informed_entity.getRouteId();
              }
              if (informed_entity.hasRouteType()) {
                  new_informed_entity.route_type = informed_entity.getRouteType();
              }
            
              my_informed_entities.add(new_informed_entity);
        }

        String description_text = null;
        String header_text = null;

        if (alert.hasDescriptionText()) {
          description_text = alert.getDescriptionText().getTranslation(0).getText();
        }
        if (alert.hasHeaderText()) {
          header_text = alert.getHeaderText().getTranslation(0).getText();
        }

        com.iqtransit.gtfs.ServiceAlert service_alert = new com.iqtransit.gtfs.ServiceAlert(entity.getId(), alert.getCause().getNumber(), alert.getEffect().getNumber(), header_text, description_text, internal_active_period_list, my_informed_entities);

        results.add(service_alert);

        }

        return results; 
  }

}