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
public class AgencyTest {

    @Test
    public void thisAlwaysPasses() {

    	AgencyInterface a = new MBTAAgency();
    	//return true;
    	org.junit.Assert.assertEquals("failure - strings are not equal", "text", a.getLinePaths("Fitchburg").length);
    	org.junit.Assert.assertEquals("failure - strings are not equal", "text", "text");
    }

    @Test
    public void thisAlwaysPasses2() {

    	AgencyInterface a = new MBTAAgency();
    	//return true;
    	 org.junit.Assert.assertEquals("failure - strings are not equal", "text", "text");
    	 org.junit.Assert.assertEquals("failure - strings are not equal", "text", "text");
    }

    @Test
    @Ignore
    public void thisIsIgnored() {
    }
}