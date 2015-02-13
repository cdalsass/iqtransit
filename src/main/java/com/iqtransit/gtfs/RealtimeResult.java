package com.iqtransit.gtfs;
import com.iqtransit.db.MySQL; 
import java.sql.SQLException;

public interface RealtimeResult {
	
	public boolean store(MySQL mysql) throws SQLException;

}