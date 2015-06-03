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

/* contains many of the GTFS database-specific functions, which are likely to vary based on agency. */

public abstract class Agency {

	protected Properties config;
	protected Connection conn; 


	/* find the trip id from short name. depends on time becuase multiple services may be running at the moment */

	public String getTripIdFromShortNameNow(String trip_short_name, long reference_time_seconds) throws SQLException {

		String[] services = getServicesIdsFromShortName(trip_short_name);
		String active_service = null; 
		for (int i = 0; i < services.length; i++ ) {

			if (isServiceRunningNow(services[i],reference_time_seconds) == true) {
				// i stores the service id we are working with 
				active_service = services[i];
				break;
			}

		}

		if (active_service == null) {
			return null;
		}

		return this.getTripIdFromServiceAndShortName(active_service, trip_short_name);
		
	}

	/* simply queries trips database with named params */
	public String getTripIdFromServiceAndShortName(String service_id, String trip_short_name) throws SQLException {

		String sql = "select trip_id from trips where service_id = ? and trip_short_name = ?";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, service_id);
        stmt.setString(2, trip_short_name);

        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String trip_id = results.getString("trip_id");
        	stmt.close();
        	results.close();
        	return trip_id;
        }
	}

	/* high level function taking into account holidays, weekends and weekdays */

	public boolean isServiceRunningNow(String service_id, long reference_time_seconds) throws SQLException {

		int exception_status = this.isServiceExceptionNow(service_id, reference_time_seconds);

		if (exception_status == 2) {
			return false;
		} else if (exception_status == 1) {
			return true;
		} else {

			if (this.isServiceRunningNormallyNow(service_id, reference_time_seconds) == true) {
				return true;
			} else {
				return false;
			}

		}

	}


	public abstract TimeZone getTimeZone(); 

	public boolean isServiceRunningNormallyNow(String service_id, long reference_time_seconds) throws SQLException {

		if (this.conn == null) {
			throw new SQLException("connection missing from MBTAAgency. be sure to call assignConnection()");
		}

		GtfsDate date = new GtfsDate();

		TimeZone tz = this.getTimeZone();

		String sql = "select monday,tuesday,wednesday,thursday,friday,saturday,sunday from calendar where service_id = ? and str_to_date(start_date,'%Y%m%d') <= str_to_date('" + date.fromUnix(reference_time_seconds,tz) + "', '%Y%m%d')  and end_date >= str_to_date('" + date.fromUnix(reference_time_seconds,tz) + "','%Y%m%d')";	

		System.out.println(sql);

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, service_id);

        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	results.close();
        	return false;
        }

        int monday, tuesday, wednesday, thursday, friday, saturday, sunday;
        
        monday = results.getInt("monday");
        tuesday = results.getInt("tuesday");
        wednesday = results.getInt("wednesday");
        thursday = results.getInt("thursday");
        friday = results.getInt("friday");
        saturday = results.getInt("saturday");
        sunday = results.getInt("sunday");
        stmt.close();
        results.close();
        
        
        // find out the day of week by parsing that string formatted date, which takes into account "train time"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        
        Date dt2;

        try {
			dt2 = sdf.parse(date.fromUnix(reference_time_seconds,tz));
        } catch (Exception e) {
        	System.out.println(e.toString());
        	return false; 
        }

		Calendar calendar = Calendar.getInstance(tz);

		calendar.setTime(dt2);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		// probably not the slickest way to do this. 
		if (monday == 1 && dayOfWeek == calendar.MONDAY) {
			return true;
		} else if (tuesday == 1 && dayOfWeek == calendar.TUESDAY) {
			return true;
		} else if (wednesday == 1 && dayOfWeek == calendar.WEDNESDAY) {
			return true;
		} else if (thursday == 1 && dayOfWeek == calendar.THURSDAY) {
			return true;
		} else if (friday == 1 && dayOfWeek == calendar.FRIDAY) {
			return true;
		} else if (saturday == 1 && dayOfWeek == calendar.SATURDAY) {
			return true;
		} else if (sunday == 1 && dayOfWeek == calendar.SUNDAY) {
			return true;
		} 
        
        return false;

	}


	/* for use in current predictions. tells whether today is a service exception. useful for message "not running today due to holiday/special event" */ 

	public int isServiceExceptionNow(String service_id, long reference_time_seconds) throws SQLException {

		/* constants belong elsewhere */
		 final int SERVICE_REMOVED = 2;
		 final int SERVICE_ADDED = 1;

		if (this.conn == null) {
			throw new SQLException("connection missing from MBTAAgency. be sure to call assignConnection()");
		}

		TimeZone tz = this.getTimeZone();

		GtfsDate date = new GtfsDate();

		String sql = "select exception_type from calendar_dates where service_id = ? and date = '" + date.fromUnix(reference_time_seconds,tz) + "'";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, service_id);

        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return 0;
        } else {
        	int exception_type =  results.getInt("exception_type"); /* return SERVICE_REMOVED which is pretty much guarantee service is not running today. likewise SERVICE_ADDED */
        	stmt.close();
        	results.close();
        	return exception_type;
	    } 
	}




	public String[] getServicesIdsFromShortName(String trip_short_name) throws SQLException {
		
		if (this.conn == null) {
			throw new SQLException("connection missing from MBTAAgency. be sure to call assignConnection()");
		}

		ArrayList service_list = new ArrayList<String>();

		String sql = "select service_id from trips where trip_short_name = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, trip_short_name);

        ResultSet results = stmt.executeQuery();

        while (results.next())  {
        	service_list.add(results.getString("service_id"));
        }
        
        stmt.close();
        results.close();

		return (String[]) service_list.toArray(new String[service_list.size()]);
	}



}