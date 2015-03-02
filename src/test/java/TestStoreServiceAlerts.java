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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestStoreServiceAlerts {
    
    @Test
    public void testDownloadServiceAlerts() throws IOException {
        
        Properties prop  = null;
        try {
            prop = Config.load();
        } catch (IOException e) {
            org.junit.Assert.assertEquals("should read config file", null , e.toString());
        }


        AgencyInterface mbta = new MBTAAgency();
        RealtimeSource pq2 = new ServiceAlertSource(mbta);
       
        MySQL mysql = new MySQL("jdbc:mysql://" + prop.getProperty("dbhost") + ":3306/"  + prop.getProperty("database") + "?user=" + prop.getProperty("dbuser") + "&password=" +prop.getProperty("dbpassword"));
        
        try {
            mysql.connect();
            System.out.println("just connected");
        } catch (Exception e) {
            System.out.println("unable to connect to database " + e.toString()); 
            org.junit.Assert.assertEquals("should connect to db", null , e.toString());
        }

        RealtimeResult pq3 = null;
        try {
            pq3 = pq2.fetch(null, "gtfs-realtime", null);
        } catch (Exception e) {
            org.junit.Assert.assertEquals("fetch failed ", null , e.toString());
        }
        //System.out.println(pq2.dump());
        org.junit.Assert.assertEquals("should have loaded some bytes", true, pq2.getLoadedBytes().length > 0 );

        ArrayList<RealtimeEntity> list_of_results = pq3.parse();       
        org.junit.Assert.assertEquals("should have parsed some" ,true, list_of_results.size() >= 0 );

        for(RealtimeEntity realtimeresult: list_of_results) {       
            
            //System.out.println(realtimeresult.toString());   
            ServiceAlert sa = (ServiceAlert) realtimeresult;
            

           try {    
                org.junit.Assert.assertEquals("should be able to insert record" , true , realtimeresult.store(mysql.getConn()));   
                
            } catch (SQLException e) {

                org.junit.Assert.assertEquals("should never hit an exception " , null , e.toString()); 
                System.out.println("database error storing " + e.toString());

            }

        //System.out.println("retrieved element: " + item);
        }
        

        try {
            mysql.close();
        } catch (SQLException e) {
            System.out.println("Error closing " + e.toString());
        }
    } 
}