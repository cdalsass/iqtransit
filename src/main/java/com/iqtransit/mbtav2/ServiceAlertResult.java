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
        		System.out.println("HERE IS THE ID" + jso.get("alert_id").getAsInt());
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