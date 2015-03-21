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


public class TestSpecificServiceAlerts {

    @Test
    public void testSpecificAlerts() throws IOException {
        
        AgencyInterface mbta = new MBTAAgency();
        RealtimeSource pq2 = new ServiceAlertSource(mbta,"GTFSRT");
        
        RealtimeResult pq3 = pq2.loadLocalFile("/Users/cdalsass/dev/iqtransit/src/test/test_data/Alerts.pb");
        //System.out.println(pq2.dump());;

        org.junit.Assert.assertEquals("should have loaded some bytes", true, pq2.getLoadedBytes().length > 1000 );

        ArrayList<RealtimeEntity> list_of_results = pq3.parse();       
        org.junit.Assert.assertEquals("should have parsed some" ,true, list_of_results.size() >= 10);

        for(RealtimeEntity realtimeresult: list_of_results) {   
            
            ServiceAlert sa = (ServiceAlert) realtimeresult;
            
            if ("26578".equals(sa.id))    {
                org.junit.Assert.assertEquals("26578 should have 1 active periods" , 1,  sa.active_periods.size());
            }

            if ("63154".equals(sa.id)) {
                org.junit.Assert.assertEquals("63154 should have 3 informed entities" , 3,  sa.informed_entities.size());
            }

        } 

    }

    @Test
    public void testSpecificAlerts2() throws IOException {
        
        AgencyInterface mbta = new MBTAAgency();
        RealtimeSource pq2 = new ServiceAlertSource(mbta,"GTFSRT");

        RealtimeResult alerts_2015_03_02 = pq2.loadLocalFile("/Users/cdalsass/dev/iqtransit/src/test/test_data/Alerts_2015_03_02.pb");
        System.out.println(alerts_2015_03_02.dump(pq2.getLoadedBytes()));
        ArrayList<RealtimeEntity> list_of_results2 = alerts_2015_03_02.parse();   

 //org.junit.Assert.assertEquals("test",false,true);
        for(RealtimeEntity realtimeresult2: list_of_results2) {   
            ServiceAlert sa = (ServiceAlert) realtimeresult2;

            if ("66141".equals(sa.id))    {
                org.junit.Assert.assertEquals("66141 should have 1 informed_entities" , 1,  sa.informed_entities.size());
                org.junit.Assert.assertEquals("66141 should have null for route_id (because it's empty)" , null,  sa.informed_entities.get(0).route_id);
                org.junit.Assert.assertEquals("66141 should have null for route_type (because it's empty in feed)" , null,  sa.informed_entities.get(0).route_type);
            }
        }   

    }

}