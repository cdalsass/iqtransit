package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeSource;
import com.iqtransit.agency.AgencyInterface;
import com.iqtransit.gtfs.RealtimeResult;
import com.iqtransit.gtfs.ServiceAlertResult;


/* responsible for parsing and downloading from remote source. knows about Google GTFS. */

public class ServiceAlertSource extends RealtimeSource {

	public ServiceAlertSource(AgencyInterface agency) {
			super(agency);
	}

	public String GetDownloadUrl(String line, String format) {
		return this.agency.ServiceAlertUrl();
	}

	// this associates the corrolary results class with the source. 
	public RealtimeResult Result() {
		return new ServiceAlertResult(this);
	}
	
}