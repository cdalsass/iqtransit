package com.iqtransit.mbtav2;
import java.util.ArrayList;

import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import com.iqtransit.common.*;

import java.io.IOException;
import java.util.List;

public class ServiceAlertResult extends RealtimeResult {
	
	public ServiceAlertResult(RealtimeSource rts) {
			super(rts);
	}

	public ArrayList<RealtimeEntity> parse() {
		System.out.println(this.source.getLoadedBytes());
        ArrayList<RealtimeEntity> results = new ArrayList<RealtimeEntity>();

        return results; 
  	}

  	public String dump(byte [] b) {
  		return null;
  	}

}