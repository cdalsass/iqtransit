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
import java.util.List;
import com.iqtransit.geo.Algorithms;
import java.util.Collections;
import com.iqtransit.agency.UpcomingTrain;
import com.iqtransit.gtfs.GtfsTime;
import java.util.Arrays;
/* contains many of the GTFS database-specific functions, which are likely to vary based on agency. */

public abstract class Agency {

	protected Properties config;
	protected Connection conn; 
	protected String id; 

	public String getId() {
		return id;
	}
	
	// ton's of duplicated code here.... not sure if we should factor or not....

	public String getStartStopName(String trip_id)  throws SQLException {

		String sql = "select stop_id from stop_times where trip_id = ? order by stop_sequence asc limit 1";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, trip_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String stop_id = results.getString("stop_id");
        	stmt.close();
        	results.close();
        	return this.getStopName(stop_id);
        }

	}

	public String getStartStopId(String trip_id)  throws SQLException {

		String sql = "select stop_id from stop_times where trip_id = ? order by stop_sequence asc limit 1";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, trip_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String stop_id = results.getString("stop_id");
        	stmt.close();
        	results.close();
        	return stop_id;
        }

	}


	public String getTerminalStopName(String trip_id)  throws SQLException {

		String sql = "select stop_id from stop_times where trip_id = ? order by stop_sequence desc limit 1";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, trip_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String stop_id = results.getString("stop_id");
        	stmt.close();
        	results.close();
        	return this.getStopName(stop_id);
        }

	}

	public String getTerminalStopId(String trip_id)  throws SQLException {

		String sql = "select stop_id from stop_times where trip_id = ? order by stop_sequence desc limit 1";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, trip_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String stop_id = results.getString("stop_id");
        	stmt.close();
        	results.close();
        	return stop_id;
        }

	}

	public String getTerminalTime(String trip_id)  throws SQLException {

		String sql = "select arrival_time from stop_times where trip_id = ? order by stop_sequence desc limit 1";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, trip_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String arrival_time = results.getString("arrival_time");
        	stmt.close();
        	results.close();
        	return arrival_time;
        }

	}

	public String getStartTime(String trip_id)  throws SQLException {

		String sql = "select departure_time from stop_times where trip_id = ? order by stop_sequence limit 1";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, trip_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String departure_time = results.getString("departure_time");
        	stmt.close();
        	results.close();
        	return departure_time;
        }

	}

	public String getStopName(String stop_id)  throws SQLException {

		String sql = "select stop_name from stop_locations where stop_id = ?";	

		PreparedStatement stmt = conn.prepareStatement(sql);
        
        stmt.setString(1, stop_id);
  
        ResultSet results = stmt.executeQuery();
 
        if (!results.next()) {
        	 // returned no records from results set so not special day 
        	stmt.close();
        	results.close();
        	return null;
        } else {
        	String stop_name = results.getString("stop_name");
        	stmt.close();
        	results.close();
        	return stop_name;
        }

	}

	public ArrayList<UpcomingTrain> getTripsFromStop(String stop_id, String [] current_service_ids) throws SQLException {

		if (this.conn == null) {
			throw new SQLException("connection missing from " + this.getId() + "Agency. be sure to call assignConnection()");
		}

		String sql = "select stop_times.stop_id, trips.trip_id, routes.route_id, route_long_name, arrival_time, service_id, trip_headsign, trip_short_name, trips.trip_id FROM trips, stop_times, routes WHERE trips.trip_id = stop_times.trip_id AND stop_times.stop_id = ? and routes.route_id = trips.route_id";

		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, stop_id);

		ResultSet results = stmt.executeQuery();

                ArrayList<UpcomingTrain> upcoming_trains = new ArrayList<UpcomingTrain>();

                while (results.next())  {
                        
                	// if service id is included in list, add it. 
                	// just adding lots of fields. not totally sure if this will be inneficient, but most lookups are 1 table and very fast.
                	if (Arrays.asList(current_service_ids).contains(results.getString("service_id"))) {

                		upcoming_trains.add(new UpcomingTrain( 
                			results.getString("trip_id"), 
                			results.getString("trip_short_name"), 
                			results.getString("arrival_time"), 
                			this.getStopName(results.getString("stop_id")), // stop_name
                			results.getString("stop_id"), 
                			this.getStartTime(results.getString("trip_id")), // start time
                			this.getTerminalTime(results.getString("trip_id")), // terminal time
                			this.getStartStopId(results.getString("trip_id")), // start_stop_id 
                			this.getTerminalStopId(results.getString("trip_id")), //terminal_stop_id
                			this.getTerminalStopName(results.getString("trip_id")), // terminal_stop_name
                			results.getString("route_id"),
                                        results.getString("route_long_name")));

                	}
                }

                stmt.close();
                results.close();
                return upcoming_trains;

	}

	/* route id can be null */

	public String[] getServicesIdsRunningNow(int route_type, String route_id, long reference_time_seconds) throws SQLException {

		if (this.conn == null) {
			throw new SQLException("connection missing from " + this.getId() + "Agency. be sure to call assignConnection()");
		}

		String sql = "select distinct service_id as service_id from routes, trips where routes.route_type = " + route_type + " and routes.route_id = trips.route_id";

		if (route_id != null) {
			sql += " and routes.route_id = ?"; 
		}

                PreparedStatement stmt = conn.prepareStatement(sql);
                
                if (route_id != null) {
                	stmt.setString(1, route_id);
                }

                ResultSet results = stmt.executeQuery();

                List<String> route_ids = new ArrayList<String>();

                while (results.next())  {
                	if (isServiceRunningNow(results.getString("service_id"),  reference_time_seconds)) {
                		route_ids.add(results.getString("service_id"));
                	}
                }

                stmt.close();
                results.close();

                return (String[]) route_ids.toArray(new String[route_ids.size()]);

	}


	/* simple class to store distance and stop id becuase ArrayList<String,Double> doesn't work */
	private class DistanceToStopId implements Comparable<DistanceToStopId> {

		public DistanceToStopId(String stop_id, Double distance) {
			this.stop_id = stop_id;
			this.distance = distance;
		}
		public Double distance;
		public String stop_id;

		@Override
	    public int compareTo(DistanceToStopId compareto) {
	        return Double.compare(this.distance, compareto.distance);
	    }
	}

	public String[] getClosestStopIds(String route_id, double latitude, double longitude, int route_type) throws SQLException {

		// if route is null, find closest regardless of route. 

		// this query is 2s. 
		// select distinct stop_locations.stop_id, stop_locations.stop_lat, stop_locations.stop_lon from routes, trips, stop_times, stop_locations where routes.route_type = 2 and routes.route_id = trips.route_id and trips.trip_id = stop_times.trip_id and stop_times.stop_id = stop_locations.stop_id order by departure_time;

		// this query is .15s
		// select service_id, departure_time from routes, trips, stop_times, stop_locations where routes.route_type = 2 and routes.route_id = trips.route_id and trips.trip_id = stop_times.trip_id and stop_times.stop_id = stop_locations.stop_id and stop_locations.stop_id = 'Fitchburg';

		if (this.conn == null) {
			throw new SQLException("connection missing from " + this.getId() + "Agency. be sure to call assignConnection()");
		}

		/* simple way to increase performance of this method to index stop_id, location (lat, long), and route_id. now we can directly query index table instead of 4 tables. */

		String sql = "select * from stop_location_index where route_type = " + route_type;

		if (route_id != null) {
			sql += " and routes.route_id = ?"; 
		}

		List<DistanceToStopId> locations_distances = new ArrayList<DistanceToStopId>();

        PreparedStatement stmt = conn.prepareStatement(sql);
        
        if (route_id != null) {
        	stmt.setString(1, route_id);
        }

        ResultSet results = stmt.executeQuery();

        while (results.next())  {
        	locations_distances.add(new DistanceToStopId(results.getString("stop_id"), Algorithms.distanceTo(latitude, longitude, results.getDouble("lat"),results.getDouble("longitude"))));
        }
        
        stmt.close();
        results.close();

        Collections.sort(locations_distances);

        String [] stop_id_list = new String[locations_distances.size()];

        // probably more elegant ways to do this.
        for (int i = 0; i < locations_distances.size(); i++) {
        	stop_id_list[i] = locations_distances.get(i).stop_id;
		}

		return stop_id_list;

	} 

	/* find the trip id from short name. depends on time because multiple services may be running at the moment */

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
                        // remove service.
			return false;
		} else if (exception_status == 1) {
                        // add
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
			throw new SQLException("connection missing from " + this.getId() + "Agency. be sure to call assignConnection()");
		}

		GtfsDate date = new GtfsDate();

		TimeZone tz = this.getTimeZone();

		String sql = "select monday,tuesday,wednesday,thursday,friday,saturday,sunday from calendar where service_id = ? and str_to_date(start_date,'%Y%m%d') <= str_to_date('" + date.fromUnix(reference_time_seconds,tz) + "', '%Y%m%d')  and end_date >= str_to_date('" + date.fromUnix(reference_time_seconds,tz) + "','%Y%m%d')";	

	
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
			throw new SQLException("connection missing from " + this.getId() + "Agency. be sure to call assignConnection()");
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
			throw new SQLException("connection missing from " + this.getId() + "Agency. be sure to call assignConnection()");
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