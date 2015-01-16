// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;
import com.iqtransit.gtfs.PredictionQuery;
import com.iqtransit.agency.*;
import java.util.Date;
/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class GTFSTest {

    private static GtfsRealtime.FeedMessage parseFeed(String path) throws IOException {
        FileInputStream fin = new FileInputStream(path);
        CodedInputStream in = CodedInputStream.newInstance(fin);
        GtfsRealtime.FeedMessage.Builder b = GtfsRealtime.FeedMessage.newBuilder();

        b.mergeFrom(in, null);
        fin.close();

        return b.build();
    }


    @Test
    public void testDisplay() {
        
        AgencyInterface mbta = new MBTAAgency();
        PredictionQuery pq = new PredictionQuery(mbta);
        pq.fetchPrediction(null, "gtfs-realtime", null);
        System.out.println(pq.toString()); // for debugging.
        pq.store();

        try {
            //GtfsRealtime rt = new GtfsRealtime();
            GtfsRealtime.FeedMessage.Builder b = GtfsRealtime.FeedMessage.newBuilder();
            GtfsRealtime.FeedMessage feed = parseFeed("/tmp/TripUpdates.pb");
            List<GtfsRealtime.FeedEntity>  entities = feed.getEntityList();
            org.junit.Assert.assertEquals("should be at least a few entities", true, entities.size() > 0 );
        } catch (Exception t) {
            org.junit.Assert.assertEquals("should not throw exception " + t, true, false);
            System.err.println(t);
        }
        //    org.junit.Assert.assertEquals("should be at least a few entities", true, entities.size() > 0 );
    }

}