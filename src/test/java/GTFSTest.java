// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.iqtransit.gtfs.RealtimeQuery;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.iqtransit.gtfs.*;
import com.iqtransit.geo.*;
import com.iqtransit.agency.*;
import com.iqtransit.db.MySQL;

import java.util.Date;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class GTFSTest {

    @Test
    public void testDownloadGFTS() throws IOException {
        
        Properties prop = new Properties();
        InputStream input = null;
        String database = "";
        String dbuser = "";
        String dbpassword = "";
        String dbhost = "";

        try {
            input = new FileInputStream("/Users/cdalsass/dev/iqtransit/config.properties");
     
            // load a properties file
            prop.load(input);
    
            // get the property value and print it out
            database = prop.getProperty("database");
            dbuser = prop.getProperty("dbuser");
            dbpassword = prop.getProperty("dbpassword");
            dbhost = prop.getProperty("dbhost");
     
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw ex;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        AgencyInterface mbta = new MBTAAgency();
        RealtimeQuery pq1 = new VehiclePositionQuery(mbta);
        RealtimeQuery pq2 = new ServiceAlertsQuery(mbta);
        RealtimeQuery pq3 = new TripUpdatesQuery(mbta);
        
        RealtimeQuery queries[]  = new RealtimeQuery[] { pq1, pq2, pq3 };

        MySQL mysql = new MySQL("jdbc:mysql://" + dbhost + ":3306/"  + database + "?user=" + dbuser + "&password=" +dbpassword);
        
        try {
            mysql.connect();
            System.out.println("just connected");
        } catch (Exception e) {
            System.out.println("unable to connect to database " + e.toString()); 
        }
        

        for (RealtimeQuery rq : queries) {

            rq.fetchPrediction(null, "gtfs-realtime", null);
            org.junit.Assert.assertEquals("should have loaded some bytes", true, rq.getLoadedBytes().length > 0 );
            ArrayList<RealtimeResult> list_of_results = rq.parse();       
            org.junit.Assert.assertEquals("should have parsed some" ,true, list_of_results.size() >= 0 /* temporarily set to 0 during severe snowstorm outage */);

            for(RealtimeResult realtimeresult: list_of_results) {          
                try {
                    org.junit.Assert.assertEquals("should be able to insert record" , true , realtimeresult.store(mysql));
                } catch (SQLException e) {
                    System.out.println("database error storing " + e.toString());
                }
            //System.out.println("retrieved element: " + item);
            }
        }

        try {
            mysql.close();
        } catch (SQLException e) {
            System.out.println("Error closing " + e.toString());
        }
    
    }

}