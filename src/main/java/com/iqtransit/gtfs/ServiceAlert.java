package com.iqtransit.gtfs;
import com.iqtransit.db.MySQL;
import com.iqtransit.gtfs.RealtimeResult;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types; 
import java.sql.SQLException;
import java.sql.Connection;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import java.util.ArrayList;

/* Core code responsible for data structure of a service alert. Keeps me from being dependent on Google GTFS throughout my app. Currently also maps data within this structure to a data table in SQL. */

public class ServiceAlert implements RealtimeResult {

	public int cause;
	public int effect;
  public String id;
  public String description_text;
  public String header_text; 

  public ArrayList<TimeRange> active_periods = new ArrayList<TimeRange>();
  public ArrayList<Entity> informed_entities = new ArrayList<Entity>();

/*
    CREATE TABLE `service_alert` (
  `id` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `cause` int(11) NOT NULL,
  `effect` int(11) NOT NULL,
  `description_text` text NULL,
  `header_text` text null,
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx23453` (`id`)
);

CREATE TABLE service_alert_active_periods (
  alert_id int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  start double null,
  end double null
);

CREATE TABLE `service_alert_informed_entities` (
  `alert_id` int(11) NOT NULL,
  `agency_id` varchar(255) not null,
  `created` datetime DEFAULT NULL,
  `route_id` varchar(255)  null, # this can be null
  `route_type` varchar(255) not null
);

*/

    public ServiceAlert(String id, int cause,  int effect, String description_text, String header_text, ArrayList<TimeRange> active_periods, ArrayList<Entity> informed_entities) {
        this.cause = cause;
        this.effect = effect;
        this.id = id;
        this.description_text = description_text;
        this.header_text = header_text;
        this.active_periods = active_periods;
        this.informed_entities = informed_entities;
    }
	
	  public String toString() {
        return id + " (" + cause + ", " + effect + ","  + description_text + ")";
    }

    public boolean clearStore(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM service_alert where id = " + this.id);
        return true;
    }

    public boolean store(Connection conn) throws SQLException {

        /* making a simplification. i am only going to store one alert per id. this means you could lose information, say if a date changes for the same id #. check for existing entries in there. */

        Statement stmt3 = conn.createStatement();
        ResultSet rs3 = stmt3.executeQuery("SELECT COUNT(*) as count FROM service_alert where id = " + this.id);
        rs3.next();
        int count = rs3.getInt("count");

        if (count == 0) {

          String query = "INSERT INTO service_alert (id, created, cause, effect, header_text, description_text) VALUES (?, now(),?,?,?,?)";
   
          PreparedStatement preparedStmt = conn.prepareStatement(query);
          preparedStmt.setString (1, this.id);
          preparedStmt.setInt (2, this.cause);
          preparedStmt.setInt (3, this.effect);
          preparedStmt.setString (5, this.header_text);
          preparedStmt.setString (4, this.description_text);
          preparedStmt.execute();

          for (Entity informed_entity: this.informed_entities) {
            query = "INSERT INTO service_alert_informed_entities (alert_id, agency_id, route_id, route_type, created) VALUES (?,?,?,?,now())";
            PreparedStatement preparedStmt2 = conn.prepareStatement(query);
            preparedStmt2.setString (1, this.id);
            preparedStmt2.setString (2, informed_entity.agency_id);
            preparedStmt2.setString (3, informed_entity.route_id);
            preparedStmt2.setInt (4, informed_entity.route_type);
            preparedStmt2.execute();
          }

          for (TimeRange range: this.active_periods) {
            query = "INSERT INTO service_alert_active_periods (alert_id, start, end, created) VALUES (?,?,?,now())";
            PreparedStatement preparedStmt3 = conn.prepareStatement(query);
            preparedStmt3.setString (1, this.id);
            preparedStmt3.setDouble (2, range.start);
            if (range.end != null) {
              preparedStmt3.setDouble (3, range.end);
            } else {
              preparedStmt3.setNull(3,Types.NULL);
            }
            preparedStmt3.execute();
          }
          
          return true; 

        } else {
          // let caller know nothing was entered. 
          return false;    
        }   
    }
}