package com.iqtransit.gtfs;
import com.iqtransit.geo.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LocationStore {
	
	private final String url;
	private Connection conn;
	
	public LocationStore( String url) {
		this.url = url;
	}

	public boolean connect() throws Exception {

		try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw ex;
        }

		this.conn = null;
		
		try {
 			this.conn = DriverManager.getConnection(this.url);
		} catch (SQLException ex) {
		    // handle any errors
		    throw ex; 
		} finally {
			// clean up 
		}

		/*
		CREATE TABLE `prediction_monitor` (
		  `id` int(11) NOT NULL AUTO_INCREMENT,
		  `created` datetime DEFAULT NULL,
		  `vehicle_id` varchar(500) NOT NULL,
		  `speed` double NOT NULL,
		  bearing double not null,
		  longitude double not null,
		  latitude double not null,
		  PRIMARY KEY (`id`),
		  UNIQUE KEY `idx2345` (`id`,`vehicle_id`)
		) ENGINE=MyISAM AUTO_INCREMENT=828 DEFAULT CHARSET=latin1 
		*/

	
		
		return true; 
	}

	public boolean close() throws SQLException{
		conn.close();	
		return true; 
	}

	public boolean store(Locatable l) throws SQLException {

		String query = "INSERT INTO prediction_monitor (id, created, vehicle_id, speed, bearing, longitude, latitude) values (null, now(), ?,?,?,?,?)";
 
    	PreparedStatement preparedStmt = this.conn.prepareStatement(query);
    	preparedStmt.setString (1, l.id);
    	preparedStmt.setDouble (2, l.speed);
    	preparedStmt.setDouble (3, l.bearing);
    	preparedStmt.setDouble (4, l.longitude);
    	preparedStmt.setDouble (5, l.latitude);
 		preparedStmt.execute();
		//System.out.println("tripid = " + l.id + " bearing = " + l.bearing + " speed = " + l.speed + " latitude = " + l.latitude + " longitude" + l.longitude);
		return true; 
	}

}