package com.iqtransit.common;
import com.iqtransit.agency.AgencyInterface;

/* responsible for parsing and downloading from remote source. knows about Google GTFS. */

public class ServiceAlertSource extends RealtimeSource {

	public ServiceAlertSource(AgencyInterface agency, String format) {
			super(agency, format);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.ServiceAlertUrl();
	}

	// this associates the corrolary results class with the source. 
	public RealtimeResult Result() throws IllegalArgumentException {
		// eventually we'll want a factory class for this.
		if (this.format == "GTFSRT") {
			return new com.iqtransit.gtfs.ServiceAlertResult(this);
		} else if (this.format == "MBTART-V2") {
			return new com.iqtransit.mbtartv2.ServiceAlertResult(this);
		} else {
			throw new IllegalArgumentException("invalid format '" + this.format + "'");
		}

	}
	
}