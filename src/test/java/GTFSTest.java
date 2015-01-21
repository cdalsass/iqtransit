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

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class GTFSTest {

    @Test
    public void testDownloadGFTS() {
        
        AgencyInterface mbta = new MBTAAgency();
        PredictionQuery pq = new PredictionQuery(mbta);
        pq.fetchPrediction(null, "gtfs-realtime", null);
        org.junit.Assert.assertEquals("should have loaded some bytes", true, pq.getLoadedBytes().length > 0 );
        ArrayList<Locatable> l = pq.parse();
        LocationStore s = new LocationStore();

        for(Locatable locatable: l){
            // pq.store();
            s.store(locatable);
            //System.out.println("retrieved element: " + item);
        }

//Read more: http://javarevisited.blogspot.com/2011/05/example-of-arraylist-in-java-tutorial.html#ixzz3PH2GeCEM

  //      System.out.println(pq.toString()); // for debugging.
                
    }

}