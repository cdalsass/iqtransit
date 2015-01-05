// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.iqtransit.server.LocatableList;
import com.iqtransit.geo.LocatableFormatter;
import com.iqtransit.geo.LocatableJSONFormatter;
import com.iqtransit.geo.Locatable;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class LocatableListTest {

    @Test
    public void testAddingAndDeduping() {

    	LocatableList a = new LocatableList();
    	String id = "user1000";
        double lat = 72.00;
        double longitude = 72.00;
        a.locationUpdate(id, lat, longitude);
        a.getLocations();
        org.junit.Assert.assertEquals("size() should be 1 item in last after 1 unique id used",1, a.size());
        a.locationUpdate("different user", 73.00, longitude);
        org.junit.Assert.assertEquals("should be 2 items in list after 2 unique ids used", 2, a.size());
        a.locationUpdate(id, 71.00, longitude);
        org.junit.Assert.assertEquals("based on id number, dups should be eliminated", 2, a.size());
        a.locationUpdate("different user2", 73.00, longitude);
        org.junit.Assert.assertEquals("make sure update took place",a.get(id).latitude, 71.00, .001);

    }

    @Test
    public void testLocationFunctions() {

        Locatable from = new Locatable("from",42.520536, -71.507640);
        Locatable to = new Locatable("to",42.501555, -71.476741);
        org.junit.Assert.assertEquals("distance should be about 2 miles", true, from.distanceTo(to) > 1.5 &&  from.distanceTo(to) < 2.5);

    }

    @Test
    public void testDisplay() {

        LocatableList a = new LocatableList();
        String id = "user1000";
        double lat = 72.00;
        double longitude = 72.00;
        a.locationUpdate(id, lat, longitude);

        LocatableFormatter lf  = new LocatableJSONFormatter(); /* formats a single Locatable */
        Locatable loc = lf.parse(" { id: \"aaa\",latitude: 9.9999, longitude: 72.837 } ");
        String json_again = lf.format(loc);
        org.junit.Assert.assertEquals("location id matches","aaa",loc.id);
        org.junit.Assert.assertEquals("location id contains json",true,json_again.indexOf("{") == 0 );
    }

}