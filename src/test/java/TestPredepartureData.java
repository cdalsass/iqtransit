// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.iqtransit.gtfs.RealtimeSource;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.iqtransit.gtfs.*;
import com.iqtransit.geo.*;
import com.iqtransit.agency.*;
import com.iqtransit.db.MySQL;
import com.iqtransit.misc.Config;

import java.util.Date;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import java.util.Properties;


public class TestPredepartureData {

    @Test
    public void testPredepartureData() throws IOException {
        
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
            //throw e;
            org.junit.Assert.assertEquals("should connect to db", null , e.toString());
            System.out.println("unable to connect to database " + e.toString()); 
        }

        MBTAPredeparture mbta = new MBTAPredeparture();
        //mbta.fetch();
       	org.junit.Assert.assertEquals("should be able to read in file", true, mbta.loadLocalFile("/Users/cdalsass/dev/iqtransit/src/test/test_data/Departures.csv"));
        

        String csv = mbta.getCSV();
        org.junit.Assert.assertEquals("should have loaded 100+ bytes", true, csv.length() > 100 );
       // System.out.println(csv);
       
		org.junit.Assert.assertEquals("should return true from parse", true, mbta.parse());	

		/* try {
			Timestamp t = mbta.stringToTimestamp("1424111720");
			System.out.println("CONVERTED TIMESTAMP TO " + t.toString());
			org.junit.Assert.assertEquals("should include 2015", true, t.toString().indexOf("2015") != -1);
		} catch (NumberFormatException e) {
			org.junit.Assert.assertEquals("should be able to convert string to timestamp", null , e.toString());
		}
*/

        try {

        	org.junit.Assert.assertEquals("should return true from store", true, mbta.store(mysql.getConn()));
        	
        } catch (Exception e) {
        	org.junit.Assert.assertEquals("should not fail to store", null , e.toString());
        }
        

    }
}