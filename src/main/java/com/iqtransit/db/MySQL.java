package com.iqtransit.db;
import com.iqtransit.geo.Locatable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQL {
	
	private final String url;
	private Connection conn;
	
	public MySQL( String url) {
		this.url = url;
	}

	public Connection getConn() {
		return this.conn;
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

	

}