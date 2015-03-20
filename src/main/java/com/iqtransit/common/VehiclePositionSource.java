package com.iqtransit.common;
import com.iqtransit.agency.AgencyInterface;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

/* responsible for parsing and downloading from remote source. */

public class VehiclePositionSource extends RealtimeSource {
	
	public VehiclePositionSource(AgencyInterface agency, String format) {
		super(agency, format);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.VehiclePositionUrl();
	}

	// this associates the corrolary results class with the source. 
	public RealtimeResult Result() {
		if (this.format == "GTFSRT") {
			return new com.iqtransit.gtfs.VehiclePositionResult(this);
		} else {
			throw new IllegalArgumentException("invalid format '" + this.format + "'");
		}
	}

}