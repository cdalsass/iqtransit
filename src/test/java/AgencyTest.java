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
import java.util.Properties;
import com.iqtransit.misc.Config;
import java.io.IOException;
import com.iqtransit.db.MySQL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import com.iqtransit.gtfs.GtfsDate;

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class AgencyTest {

    @Test
    public void thisAlwaysPasses() {

    	AgencyInterface a = new MBTAAgency();
    	org.junit.Assert.assertEquals("failure - strings are not equal", "text", "text");
    
    }

    @Test
    public void confirmBasicGTFSFunctions() {

        Properties prop  = null;
        try {
            prop = Config.load();
        } catch (IOException e) {
            org.junit.Assert.assertEquals("should read config file", null , e.toString());
        }

        MySQL mysql = new MySQL("jdbc:mysql://" + prop.getProperty("dbhost") + ":3306/"  + prop.getProperty("database") + "?user=" + prop.getProperty("dbuser") + "&password=" +prop.getProperty("dbpassword"));
        
        try {
            mysql.connect();
            System.out.println("just connected");
        } catch (Exception e) {
            System.out.println("unable to connect to database " + e.toString()); 
            org.junit.Assert.assertEquals("should connect to db", null , e.toString());
        }
    	
        AgencyInterface agency = new MBTAAgency();
    	// nice to have: agency.isStopTimeActive(rail, stop, number, time, new Date());
        // must have: agency.isRunningToday(String short_trip_name, new Date());
        // agency.hasScheduleChanged(String short_trip_name, String stop, String date, new Date());
        // MBTA stores rail, stop, number time. we don't store a service id. 
        // MTA doesn't use a number on it's trains (I don't think)
        agency.assignConnection(mysql.getConn());
        String[] services;

        try {

            services = agency.getServicesIdsFromShortName("815");
            org.junit.Assert.assertEquals("There should be 2 services based on May 2015 GTFS. This will fail when services change and GTFS is refreshed", true, Arrays.asList(services).contains("CR-Weekday-Recovery-Providence-Dec13"));
            org.junit.Assert.assertEquals("There should be 2 services based on May 2015 GTFS. This will fail when services change and GTFS is refreshed", 2, services.length);

            GtfsDate d = new GtfsDate();
            org.junit.Assert.assertEquals("GTFS Date should return properly", "20150519", d.fromUnix(1432048600 /* May 19th,2015 11AM EDST */, agency.getTimeZone()));

            org.junit.Assert.assertEquals("GTFS Date should return properly", "20150518", d.fromUnix(1432011600 /* May 19th,2015 1AM EDST */, agency.getTimeZone()));

            agency.isServiceRunningNow("CR-Weekday-Recovery-Providence-Dec13", 1432048600 /* May 19th,2015 11AM EDST */ );


        } catch (SQLException e) {
            System.out.println(e.toString());
            org.junit.Assert.assertEquals("shouldn't hit mysql error",true, false);
        }

        


    }

    @Test
    @Ignore
    public void thisIsIgnored() {
    }
}