package com.iqtransit.gtfs;
import com.iqtransit.db.MySQL;
import com.iqtransit.gtfs.RealtimeResult;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TripUpdate implements RealtimeResult {

	public double latitude;
	public double longitude;
    public double bearing;
    public double speed;
	public String id;
    public String trip;

    public TripUpdate(String id, String trip, double latitute, double longitude, double speed, double bearing) {
        this.latitude = latitute;
        this.longitude = longitude;
        this.speed = speed;
        this.bearing = bearing; 
        this.id = id;
        this.trip = trip;
    }
	
	public String toString() {
        return id + " (" + latitude + ", " + longitude + ","  + speed + "," + bearing + ")";
    }
    
    public boolean store(MySQL mysql) throws SQLException {

        String query = "INSERT INTO prediction_monitor (id, created, vehicle_id, speed, bearing, longitude, latitude) values (null, now(), ?,?,?,?,?)";
 
        PreparedStatement preparedStmt = mysql.getConn().prepareStatement(query);
        preparedStmt.setString (1, this.id);
        preparedStmt.setDouble (2, this.speed);
        preparedStmt.setDouble (3, this.bearing);
        preparedStmt.setDouble (4, this.longitude);
        preparedStmt.setDouble (5, this.latitude);
        preparedStmt.execute();
        //System.out.println("tripid = " + l.id + " bearing = " + l.bearing + " speed = " + l.speed + " latitude = " + l.latitude + " longitude" + l.longitude);
        return true; 
    }

}