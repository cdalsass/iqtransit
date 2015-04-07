package com.iqtransit.mbtav2;
import java.util.ArrayList;

import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import com.iqtransit.common.*;

import java.io.IOException;
import java.util.List;

import com.google.gson.*;

public class ServiceAlertResult extends RealtimeResult {
	
	public ServiceAlertResult(RealtimeSource rts) {
			super(rts);
	}


	public ArrayList<RealtimeEntity> parse() {
		
		//System.out.println(this.source.getLoadedBytes());
        ArrayList<RealtimeEntity> results = new ArrayList<RealtimeEntity>();

        Gson gson = new Gson();

        try {
           	JsonObject result = gson.fromJson(new String(this.source.getLoadedBytes()), JsonObject.class);

        	for ( JsonElement a : result.getAsJsonArray("alerts"))  {
        		JsonObject jso = (JsonObject) a; /* cast this down */
        		 
            ArrayList<com.iqtransit.gtfs.Entity> my_informed_entities = new ArrayList<com.iqtransit.gtfs.Entity>();
            ArrayList<com.iqtransit.gtfs.TimeRange> internal_active_period_list = new ArrayList<com.iqtransit.gtfs.TimeRange>();
            
            com.iqtransit.gtfs.ServiceAlert service_alert = new com.iqtransit.gtfs.ServiceAlert( jso.get("alert_id").getAsString(), /* jso.get("cause")*/ 0, /*jso.get("effect") */ 0 /* temporarily hardcoding. not sure how to make these into numbers, and maybe we don't even need them */, jso.get("header_text").getAsString(), jso.get("description_text").getAsString(), internal_active_period_list, my_informed_entities);

            results.add(service_alert);
        	}


         } catch (Exception e) {
        	System.out.println("unable to parse json servicealert json");
        	return results; // just so something is returned.
        }

        return results; 
  	}

  	public String dump(byte [] b) {

  		Gson gson = new Gson();
        try {
           	Object result = gson.fromJson(new String(b), Object.class);
            return result.toString();
        } catch (Exception e) {
        	System.out.println("unable to parse json servicealert json");
        	return null;
        }
  	}

}