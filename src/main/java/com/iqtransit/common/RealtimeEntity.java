package com.iqtransit.common;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Connection;

public abstract class RealtimeEntity {
	
	public abstract boolean store(Connection conn) throws SQLException;
 	
}