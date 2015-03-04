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

import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import com.iqtransit.gtfs.TimeRange;
import com.iqtransit.gtfs.Entity;
import com.iqtransit.gtfs.ServiceAlert;
import java.io.IOException;
import java.util.List;

/* Core code responsible for data structure of a service alert. Keeps me from being dependent on Google GTFS throughout my app. Currently also maps data within this structure to a data table in SQL. */

public class ServiceAlert extends RealtimeEntity {

	public int cause;
	public int effect;
  public String alert_id;
  public Integer id;
  public String description_text;
  public String header_text; 

  public ArrayList<TimeRange> active_periods = new ArrayList<TimeRange>();
  public ArrayList<Entity> informed_entities = new ArrayList<Entity>();

/*
    CREATE TABLE `service_alert` (
    id int(11) auto_increment,
  `alert_id` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `cause` int(11) NOT NULL,
  `effect` int(11) NOT NULL,
  `description_text` text NULL,
  `header_text` text null,
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx23453` (`id`)
);

CREATE TABLE service_alert_active_periods (
  id int(11) not null,
  alert_id int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  start double null,
  end double null
);

CREATE TABLE `service_alert_informed_entities` (
  id int(11) not null,
  `alert_id` int(11) NOT NULL,
  `agency_id` varchar(255) not null,
  `created` datetime DEFAULT NULL,
  `route_id` varchar(255)  null, # this can be null
  `route_type` varchar(255) null,
  `stop_id` varchar(255)  null
);

id: "66141"
alert {
  active_period {
    start: 1425461400
    end: 1425540600
  }
  informed_entity {
    agency_id: "1"
    stop_id: "70208"
  }
  cause: UNKNOWN_CAUSE
  effect: OTHER_EFFECT
  header_text {
    translation {
      text: "Science Park elevator unavailable"
      language: "en"
    }
  }
  description_text {
    translation {
      text: "Elevator 980 SCIENCE PARK/WEST END - Street to North Station-bound platform is unavailable on Wednesday, March 4.\r\n\r\nCustomers desiring elevator service to enter on the inbound side must use the outbound elevator and utilize service to Lechmere for inbound service. Customers desiring to exit on the inbound side must continue to North Station and transfer to outbound service back to Science Park."
      language: "en"
    }
  }
}


Yileds this:

+-----+----------+-----------+---------------------+----------+------------+---------+
| id  | alert_id | agency_id | created             | route_id | route_type | stop_id |
+-----+----------+-----------+---------------------+----------+------------+---------+
| 756 |    66141 | 1         | 2015-03-02 10:28:49 | NULL     | NULL       | 70208   |
+-----+----------+-----------+---------------------+----------+------------+---------+


And yields this:

              id: 623
        alert_id: 26578
         created: 2015-03-02 10:08:03
           cause: 10
          effect: 9
description_text: The Church St @ Lexington St (layover) bus stop is temporarily closed due to construction.

Beginning Monday, April 14, 2014, the Church Street @ Lexington Street stop is temporarily closed due to construction. Customers should please travel west, turn right at Lexington Street, and travel north to the nearest accessible location on Lexington Street @ Church Street.
     header_text: Church St @ Lexington St moved


*/
  
    /* instantiate a service alert from a SQL result set. Assumes that you have not joined against any sub-tables, but that
    you don't want to query database a second time for main table data */
    
    public ServiceAlert(java.sql.ResultSet main_table_result, Connection conn) throws SQLException {

        this.cause = main_table_result.getInt("cause");
        this.effect = main_table_result.getInt("effect");
        this.id = main_table_result.getInt("id");
        this.alert_id =  main_table_result.getString("alert_id");
        this.description_text = main_table_result.getString("description_text");
        this.header_text = main_table_result.getString("header_text");

        this.informed_entities = new ArrayList<Entity>();
        this.active_periods = new ArrayList<TimeRange>();
        
        String sql = "SELECT * FROM service_alert_informed_entities where id = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, this.id);

        ResultSet results = stmt.executeQuery();

        while (results.next())  {

            com.iqtransit.gtfs.Entity new_entity = new com.iqtransit.gtfs.Entity();
            new_entity.agency_id = results.getString("agency_id");
            new_entity.route_id = results.getString("route_id");
            new_entity.route_type = results.getInt("route_type");
            new_entity.stop_id = results.getString("stop_id");
            this.informed_entities.add(new_entity);
        }

        stmt.close();

        // very similar code above!

        String sql2 = "SELECT * FROM service_alert_active_periods where id = ?";

        PreparedStatement stmt2 = conn.prepareStatement(sql2);
        stmt2.setInt(1, this.id);

        ResultSet results2 = stmt2.executeQuery();

        while (results2.next())  {

            com.iqtransit.gtfs.TimeRange new_entity2 = new com.iqtransit.gtfs.TimeRange();
            new_entity2.start = results2.getLong("start");
            new_entity2.end = results2.getLong("end");
            this.active_periods.add(new_entity2);
        }

        stmt.close();

      
    }

    public ServiceAlert(String alert_id, int cause,  int effect, String description_text, String header_text, ArrayList<TimeRange> active_periods, ArrayList<Entity> informed_entities) {
        this.cause = cause;
        this.effect = effect;
        this.alert_id = alert_id;
        this.description_text = description_text;
        this.header_text = header_text;
        this.active_periods = active_periods;
        this.informed_entities = informed_entities;
    }
	
	  public String toString() {
        return id + " (" + cause + ", " + effect + ","  + description_text + ")";
    }

    public boolean store(Connection conn) throws SQLException {

      String query = "INSERT INTO service_alert (id, alert_id, created, cause, effect, header_text, description_text) VALUES (null, ?, now(),?,?,?,?)";

      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString (1, this.alert_id);
      preparedStmt.setInt (2, this.cause);
      preparedStmt.setInt (3, this.effect);
      preparedStmt.setString (5, this.header_text);
      preparedStmt.setString (4, this.description_text);
      preparedStmt.execute();

      /*  I'm going to generate my own id instead of using theirs for canonicity.
          This means the same alert can be saved multiple times. Is that OK?
          It would probably be simpler to dedup by id. But who knows whether the alert
          will change over time - which gets missed. Plus there is an advantage
          to know which alerts are being pushed out *now* instead of having to calculate
          which alerts should be shown at any given moment.       
      */

      Statement stmt3 = conn.createStatement();
      ResultSet rs3 = stmt3.executeQuery("select last_insert_id() as last_id from service_alert");
      rs3.next();
      int last_id = rs3.getInt("last_id");


      for (Entity informed_entity: this.informed_entities) {
        query = "INSERT INTO service_alert_informed_entities (id, alert_id, agency_id, route_id, route_type, stop_id, created) VALUES (?,?,?,?,?,?,now())";
        PreparedStatement preparedStmt2 = conn.prepareStatement(query);
        preparedStmt2.setInt (1, last_id);
        preparedStmt2.setString (2, this.alert_id);
        preparedStmt2.setString (3, informed_entity.agency_id);
        preparedStmt2.setString (4, informed_entity.route_id);
        
        if (informed_entity.route_type != null) {
          preparedStmt2.setInt (5, informed_entity.route_type);
        } else {
          preparedStmt2.setNull(5,Types.NULL);
        }

        preparedStmt2.setString (6, informed_entity.stop_id);
        preparedStmt2.execute();
      }

      for (TimeRange range: this.active_periods) {
        query = "INSERT INTO service_alert_active_periods (id, alert_id, start, end, created) VALUES (?,?,?,?,now())";
        PreparedStatement preparedStmt3 = conn.prepareStatement(query);
        preparedStmt3.setInt (1, last_id);
        preparedStmt3.setString (2, this.alert_id);
        preparedStmt3.setDouble (3, range.start);
        if (range.end != null) {
          preparedStmt3.setDouble (4, range.end);
        } else {
          preparedStmt3.setNull(4,Types.NULL);
        }
        preparedStmt3.execute();
      }
          

        
        // tried returning false if nothing done, but this broke my tests.  
        return true; 
    }

    

}