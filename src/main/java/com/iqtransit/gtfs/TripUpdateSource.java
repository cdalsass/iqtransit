package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeSource;
import com.iqtransit.agency.AgencyInterface;
import java.util.ArrayList;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import java.io.IOException;
import java.util.List;

/* responsible for parsing and downloading from remote source. */

public class TripUpdateSource extends RealtimeSource {

	public TripUpdateSource(AgencyInterface agency) {
			super(agency);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.TripUpdatesUrl();
	}

	// this associates the corrolary results class with the source. 
	public RealtimeResult Result() {
		return new TripUpdateResult(this);
	}
	
}