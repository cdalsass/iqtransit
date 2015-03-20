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


public class TestSpecificTripUpdate {

    @Test
    public void testSpecificTripUpdate() throws IOException {
        
        AgencyInterface mbta = new MBTAAgency();
        RealtimeSource pq2 = new TripUpdateSource(mbta,"GTFSRT");
        
        RealtimeResult pq3 = pq2.loadLocalFile("/Users/cdalsass/dev/iqtransit/src/test/test_data/TripUpdates.pb", "gtfs-realtime");
        //System.out.println(pq2.dump());;
       System.out.println(pq3.dump(pq2.getLoadedBytes()));

        org.junit.Assert.assertEquals("should have loaded some bytes", true, pq2.getLoadedBytes().length > 1000 );

        ArrayList<RealtimeEntity> list_of_results = pq3.parse();       
        org.junit.Assert.assertEquals("parse() should give at least 10 trip updates" ,true, list_of_results.size() >= 10);

        for(RealtimeEntity realtimeresult: list_of_results) {   
            
            TripUpdate sa = (TripUpdate) realtimeresult;
            
            System.out.println(sa.toString());

            if ("1424650366_CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411".equals(sa.id))    {
                org.junit.Assert.assertEquals("1424650366_CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411 should have trip id = CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411" , "CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411" , sa.trip_id);
            }

             if ("1424650366_CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411".equals(sa.id))    {
                org.junit.Assert.assertEquals("1424650366_CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411 should have trip id = CR-Ayer" , "Ayer" , sa.stop_id);
            }

            if ("1424650366_CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411".equals(sa.id))    {
                org.junit.Assert.assertEquals("1424650366_CR-Fitchburg-CR-Sunday-Fitchburg-Aug14-2411 should have null delay" ,null, sa.delay);
            }

 
            if ("1424650366_25612159".equals(sa.id) && "110".equals(sa.stop_id))    {
                org.junit.Assert.assertEquals("1424650366_25612159 should have 0 delay" ,0, (int) sa.delay);
            }

            if ("1424650366_25612162".equals(sa.id) && sa.stop_sequence == 18)    {
                org.junit.Assert.assertEquals("1424650366_25612162 should have 0 delay" ,420, (int) sa.delay);
            }

            if ("1424650366_25612021".equals(sa.id) && sa.stop_sequence == 18 ) {
                 org.junit.Assert.assertEquals("1424650366_25612021 should have 300 delay" ,300, (int) sa.delay);
            }            

            //if ("63154".equals(sa.id)) {
            //    org.junit.Assert.assertEquals("63154 should have 3 informed entities" , 3,  sa.informed_entities.size());
            //}

        }    
    }

}