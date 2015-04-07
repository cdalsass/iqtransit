// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.iqtransit.mbtav2.*;
import com.iqtransit.common.*;
import com.iqtransit.agency.*;
import java.io.IOException;
import java.util.ArrayList;

public class TestMBTAV2ServiceAlertResult {

    @Test
    public void testServiceAlert() {
        

        AgencyInterface mbta = new MBTAAgency();
        RealtimeSource pq2 = new ServiceAlertSource(mbta,"MBTA_V2");
        RealtimeResult pq3 = null;
        try {
            
            pq3 = pq2.loadLocalFile("/Users/cdalsass/dev/iqtransit/src/test/test_data/MBTAV2Alerts.json");
            
        } catch (IOException e) {
             org.junit.Assert.assertEquals("should have loaded file successfully (verify file is actually there)", true, false);
        }

        org.junit.Assert.assertEquals("should have loaded some bytes", true, pq2.getLoadedBytes().length > 1000 );

        //System.out.println(pq3.dump(pq2.getLoadedBytes()));

        ArrayList<RealtimeEntity> alerts = pq3.parse();

        org.junit.Assert.assertEquals("should include a bunch of alerts", true, alerts.size() > 0);

        // org.junit.Assert.assertEquals("hard stop", true, false);
    }
}