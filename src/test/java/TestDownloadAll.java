// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.iqtransit.common.*;

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
import java.util.ArrayList;
import com.iqtransit.misc.Config;


/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class TestDownloadAll {

    @Test
    public void testDownloadGFTS() throws IOException {
        
        Properties prop  = null;
        try {
            prop = Config.load();
        } catch (IOException e) {
            org.junit.Assert.assertEquals("should read config file", null , e.toString());
        }

        AgencyInterface mbta = new MBTAAgency(prop);
        RealtimeSource pq1 = new VehiclePositionSource(mbta, "GTFSRT");
        RealtimeSource pq2 = new ServiceAlertSource(mbta,"GTFSRT");
        RealtimeSource pq3 = new TripUpdateSource(mbta,"GTFSRT");
        RealtimeSource pq4 = new ServiceAlertSource(mbta,"MBTA_V2");
        
        RealtimeSource queries[]  = new RealtimeSource[] { pq4 };

        MySQL mysql = new MySQL("jdbc:mysql://" + prop.getProperty("dbhost") + ":3306/"  + prop.getProperty("database") + "?user=" + prop.getProperty("dbuser") + "&password=" +prop.getProperty("dbpassword"));
        
        try {
            mysql.connect();
            System.out.println("just connected");
        } catch (Exception e) {
            //throw e;
            org.junit.Assert.assertEquals("should connect to db", null , e.toString());
            System.out.println("unable to connect to database " + e.toString()); 
        }
        
        for (RealtimeSource source : queries) {
            RealtimeResult rtr = null;

            try {
                rtr = source.fetch(null, null);
                System.out.println(rtr.dump(source.getLoadedBytes()));
                org.junit.Assert.assertEquals("should have 1k bytes (most of the time. WARNING: this may fail sometimes)", true, rtr.dump(source.getLoadedBytes()).length() > 1000 );
            } catch (Exception e) {
                org.junit.Assert.assertEquals("fetch failed ", null , e.toString());
            }

            org.junit.Assert.assertEquals("should have loaded some bytes", true, source.getLoadedBytes().length > 0 );

            ArrayList<RealtimeEntity> list_of_results = rtr.parse();  

            org.junit.Assert.assertEquals("should have parsed some" ,true, list_of_results.size() >= 0 /* temporarily set to 0 during severe snowstorm outage */);

            for(RealtimeEntity realtimeresult: list_of_results) {          
                try {
                    org.junit.Assert.assertEquals("should be able to insert record" , true , realtimeresult.store(mysql.getConn()));
                } catch (SQLException e) {
                    System.out.println("database error storing " + e.toString());
                     org.junit.Assert.assertEquals("should never hit an exception" , null , e.toString());
                }
            //System.out.println("retrieved element: " + item);
            }
        }

        // DO THIS TO STOP HERE AND VIEW OUTPUT IN STDOUT org.junit.Assert.assertEquals("forced it to quit ", null , "NOT NULL");

        try {
            mysql.close();
        } catch (SQLException e) {
            System.out.println("Error closing " + e.toString());
        }
    }
}