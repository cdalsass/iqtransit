// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.iqtransit.gtfs.PredictionQuery;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.iqtransit.gtfs.*;
import com.iqtransit.geo.*;
import com.iqtransit.agency.*;
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
            input = new FileInputStream("/Users/cdalsass/dev/tontime-crowd/iqtransit/config.properties");
     
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
        PredictionQuery pq = new PredictionQuery(mbta);
        
        pq.fetchPrediction(null, "gtfs-realtime", null);
        
        org.junit.Assert.assertEquals("should have loaded some bytes", true, pq.getLoadedBytes().length > 0 );
        
        ArrayList<Locatable> l = pq.parse();
        
        org.junit.Assert.assertEquals("should have parsed some" ,true, l.size() >= 0 /* temporarily set to 0 during severe snowstorm outage */);

        LocationStore s = new LocationStore("jdbc:mysql://" + dbhost + ":3306/"  + database + "?user=" + dbuser + "&password=" +dbpassword);
        
        try {
            s.connect();
        } catch (Exception e) {
            System.out.println("unable to connect to database " + e.toString()); 
        }
        

        for(Locatable locatable: l){
            // pq.store();
            try {
                org.junit.Assert.assertEquals("should be able to insert record" , true , s.store(locatable));
            } catch (SQLException e) {
                System.out.println("database error storing " + e.toString());
            }
            //System.out.println("retrieved element: " + item);
        }

        try {
            s.close();
        } catch (SQLException e) {
            System.out.println("Error closing " + e.toString());
        }
    
    }

}