// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.iqtransit.db.MySQL;
import com.iqtransit.gtfs.ServiceAlert;

import java.sql.*;
import com.iqtransit.misc.Config;

import java.io.IOException;
import java.util.Properties; 

public class TestServiceAlert {

    @Test
    public void testLoadFromDB() throws IOException {
        
        Properties prop  = null;
        try {
            prop = Config.load();
        } catch (IOException e) {
            org.junit.Assert.assertEquals("should read config file", null , e.toString());
        }

        MySQL mysql = new MySQL("jdbc:mysql://" + prop.getProperty("dbhost") + ":3306/"  + prop.getProperty("database") + "?user=" + prop.getProperty("dbuser") + "&password=" +prop.getProperty("dbpassword"));
        
        try {
            mysql.connect();
            System.out.println("just connected");
        } catch (Exception e) {
            System.out.println("unable to connect to database " + e.toString()); 
            org.junit.Assert.assertEquals("should connect to db", null , e.toString());
        }

        Connection conn = mysql.getConn();

        try {

            Statement stmt = conn.createStatement();
                //Code to read from database
            ResultSet results = stmt.executeQuery(
                    "SELECT * FROM service_alert where id in ( 4818, 903 ) ");
            while (results.next()) {

                String s = results.getString("alert_id");
                //JSONFormatter js = new JSONFormatter();
                //out.println(js.format(results));
                //out.println(s);
                //out.println(s);
                if (results.getString("id").equals("4818")) {
                    ServiceAlert a = new ServiceAlert(results, conn);
                    org.junit.Assert.assertEquals("alert id should be 67258", "67258" , a.alert_id);
                    org.junit.Assert.assertEquals("header text should contain Route on 7th char", 6 , a.header_text.indexOf("Route") );
                    org.junit.Assert.assertEquals("description text is null", null , a.description_text );
                    org.junit.Assert.assertEquals("should be one informed entity", 1, a.informed_entities.size());
                    org.junit.Assert.assertEquals("should be one active period", 1, a.active_periods.size());
                    
                } else if (results.getString("id").equals("903")) {
                    ServiceAlert a = new ServiceAlert(results, conn);
                    org.junit.Assert.assertEquals("should be one informed entity", 1, a.informed_entities.size());
                }
            }
        } catch (SQLException e) {
            org.junit.Assert.assertEquals("should never hit exception", null , e.toString());
        }

       

    }

}