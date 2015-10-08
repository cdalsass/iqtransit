package com.iqtransit.agency;
import java.util.Properties; 
import java.lang.IllegalArgumentException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types; 
import java.util.Date;
import java.util.TimeZone;
import com.iqtransit.gtfs.GtfsDate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.iqtransit.agency.Agency;

public class MBTAAgency extends Agency implements AgencyInterface {

	public MBTAAgency(Properties config) {
		this(); /* call constructor */
		this.config = config;
	}

	// this is possible, but you have to make sure you don't need config.
	public MBTAAgency() {
		this.id = "MBTA";
	}

	/* approximate user's closest agency. is lat/long provided within boundaries? 
		polygon comes from http://www.the-di-lab.com/polygon/
	*/

	public double[] getBoundaries() {

		double [] boundary  =	{ 42.819580715795915, -73.45458984375, 42.84777884235988, -70.0927734375, 41.60722821271716, -69.89501953125, 41.41389556467733, -70.8673095703125, 41.65239288426814, -71.795654296875, 42.370720143531976, -73.05908203125 };
		
		return boundary;

	}

	
	/* find the trip id from short name. depends on time becuase multiple services may be running at the moment */

	public String getTripIdFromShortNameNow(String trip_short_name, long reference_time_seconds) throws SQLException {

		return super.getTripIdFromShortNameNow(trip_short_name, reference_time_seconds);
		
	}

	/* simply queries trips database with named params */
	public String getTripIdFromServiceAndShortName(String service_id, String trip_short_name) throws SQLException {

		return super.getTripIdFromServiceAndShortName(service_id,trip_short_name);


	}

	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("America/New_York");
	}


	/* high level function taking into account holidays, weekends and weekdays */

	public boolean isServiceRunningNow(String service_id, long reference_time_seconds) throws SQLException {

		return super.isServiceRunningNow(service_id,reference_time_seconds);

	}


	public boolean isServiceRunningNormallyNow(String service_id, long reference_time_seconds) throws SQLException {

		return super.isServiceRunningNormallyNow(service_id, reference_time_seconds);

	}


	/* for use in current predictions. tells whether today is a service exception. useful for message "not running today due to holiday/special event" */ 

	public int isServiceExceptionNow(String service_id, long reference_time_seconds) throws SQLException {

		return super.isServiceExceptionNow(service_id, reference_time_seconds);
	
	}


	public String[] getServicesIdsFromShortName(String trip_short_name) throws SQLException {
		
		return super.getServicesIdsFromShortName(trip_short_name);

	}

	

	private int LineNumber(String line_name) {

		/* copied from tontime.js */
		int line_number = 0; 
		switch (line_name) {

			case "Greenbush" : 
				line_number = 1;
				break;
			case "Fairmount":       
				 line_number = 4;
				 break;
			case "Fitchburg":   
				line_number = 9;
				break;
			case "Worcester"  :  
				 line_number = 8;
				 break;
			case "Franklin"  :    
				line_number = 6;
				break;
			case "Haverhill" :     
				 line_number = 11;
				 break;
			case "Lowell" : 
				 line_number = 10;
				 break;
			case "Middleborough" :  
				line_number = 3;
				break;
			case "Needham":
				line_number = 7;
				break;
			case "Newburyport"   : 
				line_number = 12;
				break;
			case "Kingston/Plymouth"      : 
				line_number = 2;
				break;
			case "Providence"   : 
				line_number = 5;
				break;
			default: 
			 	System.out.println("Invalid line name.");
                break;

        }
		return line_number;
	}

	public String VehiclePositionUrl() {
		return  "http://developer.mbta.com/lib/GTRTFS/Alerts/VehiclePositions.pb";
	}


	public String ServiceAlertUrl(String format) throws IllegalArgumentException {
		if (format.equals("GTFSRT")) {
			return "http://developer.mbta.com/lib/GTRTFS/Alerts/Alerts.pb";
		} else if (format.equals("MBTA_V2")) {

			String api_key = null;
			try {
				api_key = config.getProperty("api_key");
			} catch (Exception e) {
				System.out.println("Error: api_key not defined in properties");
				throw e; 
			}
			
			return "http://realtime.mbta.com/developer/api/v2/alerts?api_key=" + config.getProperty("api_key") + "&format=json";
			 
		} else {
			throw new IllegalArgumentException();
		}
		
	}

	public String TripUpdatesUrl() {
		return "http://developer.mbta.com/lib/GTRTFS/Alerts/TripUpdates.pb";
	}

	public String RealtimeSource(String line_name, String format) {
		if (format.equals("json")) {
			int rail_id = LineNumber(line_name);
			return "http://developer.mbta.com/lib/RTCR/RailLine_" + rail_id + ".json";
		} else if (format.equals("gtfs-realtime")) {
			return "http://developer.mbta.com/lib/GTRTFS/Alerts/VehiclePositions.pb";
		} else {
			throw new Error("invalid format " + format);
		}
	}

	public String[] getLines() {

		String[] results = {
			"Greenbush",
			"Fairmount",
			"Fitchburg", 
			"Worcester",
			"Franklin",
			"Haverhill",
			"Lowell",
			"Middleborough", 
			"Needham",
			"Newburyport", 
			"Kingston/Plymouth",
			"Providence"
		};

		return results;

	}

	/* intentionally left static data (without resorting to code-gen) so this method can be used in non-database environments like Android */

	public String[] getRouteIds() {
		String [] results = {
			"CR-Fairmount",    
			"CR-Fitchburg",    
			"CR-Franklin",     
			"CR-Greenbush",    
			"CR-Haverhill",    
			"CR-Kingston",     
			"CR-Lowell",       
			"CR-Middleborough",
			"CR-Needham",      
			"CR-Newburyport",  
			"CR-Providence",   
			"CR-Worcester"   
		};
		return results;
	}

	/* maybe this should be part of a separate class, since we are only doing this to improve performance on map. */
	public double[] getReducedLinePath(String lineid) {
		return new double[3];
	}

	public double[] getClosedPoly(String lineid) {
		return new double[3];
	}

}