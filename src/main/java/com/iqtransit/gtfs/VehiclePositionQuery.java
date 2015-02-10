package com.iqtransit.gtfs;
import com.iqtransit.gtfs.RealtimeQuery;
import com.iqtransit.agency.AgencyInterface;

public class VehiclePositionQuery extends RealtimeQuery {
	public VehiclePositionQuery(AgencyInterface agency) {
			super(agency);
		}
}