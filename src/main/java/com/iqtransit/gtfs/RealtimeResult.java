package com.iqtransit.gtfs;
import java.sql.SQLException;
import java.sql.Connection;

public interface RealtimeResult {
	
	public boolean store(Connection conn) throws SQLException;
	
}