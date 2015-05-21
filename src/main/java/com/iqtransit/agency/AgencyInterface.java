package com.iqtransit.agency;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Date;
import java.util.TimeZone;

public interface AgencyInterface {

	abstract String[] getLines();
	abstract double[][] getLinePaths(String lineid); 
	abstract double[] getReducedLinePath(String lineid);
	abstract double[] getClosedPoly(String lineid);
	abstract String RealtimeSource(String lineid, String format);
	abstract String VehiclePositionUrl();
	abstract String ServiceAlertUrl(String format);
	abstract String TripUpdatesUrl();
	abstract String[] getServicesIdsFromShortName(String short_trip_name) throws SQLException;
	abstract void assignConnection(java.sql.Connection conn);
	abstract int isServiceExceptionNow(String service, long d) throws SQLException;
	abstract boolean isServiceRunningNormallyNow(String service, long d) throws SQLException;
	abstract boolean isServiceRunningNow(String service_id, long reference_time_seconds) throws SQLException;
	public String getTripIdFromServiceAndShortName(String service_id, String trip_short_name) throws SQLException;
	public String getTripIdFromShortNameNow(String trip_short_name, long reference_time_seconds) throws SQLException;
	abstract TimeZone getTimeZone();
}