package com.iqtransit.agency;

public interface AgencyInterface {

	abstract String[] getLines();
	abstract double[][] getLinePaths(String lineid); 
	abstract double[] getReducedLinePath(String lineid);
	abstract double[] getClosedPoly(String lineid);
	abstract String RealtimeSource(String lineid, String format);
	abstract String VehiclePositionUrl();
	abstract String ServiceAlertUrl();
	abstract String TripUpdatesUrl();
}