package com.iqtransit.common;
import com.iqtransit.agency.AgencyInterface;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;


/* responsible for parsing and downloading from remote source. */

public class TripUpdateSource extends RealtimeSource {

	public TripUpdateSource(AgencyInterface agency, String format) {
			super(agency, format);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.TripUpdatesUrl();
	}

	// this associates the corrolary results class with the source. 
	public RealtimeResult Result() {

		if (this.format == "GTFSRT") {
			return new com.iqtransit.gtfs.TripUpdateResult(this);
		} else {
			throw new IllegalArgumentException("invalid format '" + this.format + "'");
		}
		
	}
	
}