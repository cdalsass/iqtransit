// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;
import com.iqtransit.server.LocatableList;
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
    public void thisAlwaysPasses() {
    	LocatableList a = new LocatableList();
    	String id = "user1000";
    	double lat = 72.00;
    	double longitude = 72.00;
    	a.locationUpdate(id, lat, longitude);
    	a.getLocations();
    	org.junit.Assert.assertEquals("should be 1 item in last after 1 unique id used",1, a.size());
    	a.locationUpdate("ANOTHER user", 73.00, longitude);
    	org.junit.Assert.assertEquals("should be 2 items in list after 2 unique ids used", 2, a.size());
    	a.locationUpdate(id, lat, longitude);
    	org.junit.Assert.assertEquals("based on id number, dups should be eliminated", 2, a.size());

    }

   
}