package com.iqtransit.agency;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Date;
import java.util.TimeZone;
import com.iqtransit.agency.UpcomingTrain;
import java.util.ArrayList;

public interface AgencyInterface {

	abstract String[] getLines();
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
	public double[] getBoundaries();
	public String getId();
	public String[] getClosestStopIds(String route_id, double latitude, double longitude, int route_type) throws SQLException;
	public String[] getServicesIdsRunningNow(int route_type, String route_id, long reference_time_seconds) throws SQLException;
	public ArrayList <UpcomingTrain> getTripsFromStop(String stop_id, String [] service_ids) throws SQLException ;
	public String getStopName(String stop_id) throws SQLException;
	public String getStartTime(String trip_id)  throws SQLException;
	//public String getArrivalTime(String trip_id)  throws SQLException;
	public String getTerminalTime(String trip_id)  throws SQLException;
	public String getTerminalStopName(String trip_id)  throws SQLException;
	public String getTerminalStopId(String trip_id)  throws SQLException;
	public String getStartStopName(String trip_id)  throws SQLException ;
	public String getStartStopId(String trip_id)  throws SQLException;
	public String[] getTripIdsFromServiceId(String trip_id)  throws SQLException;
	public String[] getTripIdsFromServiceId(String[] trip_id)  throws SQLException;
	public boolean isStopOnTrip (String boarding_stop_id, String trip_id) throws SQLException;
	public String[] getDestinations(String boarding_stop_id, String [] current_service_ids) throws SQLException;
}