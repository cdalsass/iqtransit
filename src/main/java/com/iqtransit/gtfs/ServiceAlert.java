package com.iqtransit.gtfs;
import com.iqtransit.db.MySQL;
import com.iqtransit.gtfs.RealtimeResult;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceAlert implements RealtimeResult {

	public int cause;
	public int effect;
  public String id;
  public String description_text;
  public String header_text;

/*
    CREATE TABLE `service_alert_history` (
  `id` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `cause` int(11) NOT NULL,
  `effect` int(11) NOT NULL,
  `description_text` text NOT NULL,
  `header_text` text not null,
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx23453` (`id`)
)
*/

    public ServiceAlert(String id, int cause,  int effect, String description_text, String header_text) {
        this.cause = cause;
        this.effect = effect;
        this.id = id;
        this.description_text = description_text;
        this.header_text = header_text;
    }
	
	  public String toString() {
        return id + " (" + cause + ", " + effect + ","  + description_text + ")";
    }

    public boolean store(MySQL mysql) throws SQLException {

        /* making a simplification. i am only going to store one alert per id. this means you could lose information, say if a date changes for the same id # */

        String query = "REPLACE INTO service_alert_history (id, created, cause, effect, header_text, description_text) values (?, now(),?,?,?,?)";
 
        PreparedStatement preparedStmt = mysql.getConn().prepareStatement(query);
        preparedStmt.setString (1, this.id);
        preparedStmt.setInt (2, this.cause);
        preparedStmt.setInt (3, this.effect);
        preparedStmt.setString (5, this.header_text);
        preparedStmt.setString (4, this.description_text);
        preparedStmt.execute();
        
        return true; 
    }

}