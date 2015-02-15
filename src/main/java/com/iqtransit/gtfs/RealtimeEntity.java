package com.iqtransit.gtfs;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Connection;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import java.util.List;
import java.util.ArrayList;

public abstract class RealtimeEntity {
	
	public abstract boolean store(Connection conn) throws SQLException;
 	
}