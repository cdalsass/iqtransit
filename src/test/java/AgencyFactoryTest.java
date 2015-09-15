// javac -cp junit.jar com/iqtransit/*.java
// java -cp .:junit.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore com.iqtransit.AgencyTest

// javac -cp ~/Applications/android-sdk-macosx/platforms/android-20/android.jar:junit.jar com/example/foo/*.java

// java  -cp .:junit.jar:hamcrest-core-1.3.jar:/Users/cdalsass/Applications/android-sdk-macosx/platforms/android-20/android.jar org.junit.runner.JUnitCore com.example.foo.FooTest  | less

package com.iqtransit.agency;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class AgencyFactoryTest {


    @Test
    public void confirmFactory() {

        AgencyFactory af = new AgencyFactory();
        AgencyInterface generic_agency = af.createAgency("MBTA");
        org.junit.Assert.assertEquals("should match id you actually want", "MBTA", generic_agency.getId());
        AgencyInterface generic_agency2 = af.createAgency("METRA");
        org.junit.Assert.assertEquals("should match id you actually want", "METRA", generic_agency2.getId());
    }

}