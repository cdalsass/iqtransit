package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeQuery;
import com.iqtransit.agency.AgencyInterface;

public class ServiceAlertsQuery extends RealtimeQuery {
	public ServiceAlertsQuery(AgencyInterface agency) {
			super(agency);
		}
}