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
import com.iqtransit.geo.JTS;

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

            org.junit.Assert.assertEquals("GTFS Date should return properly and give day earlier because it's 12AM - 4AM", "20150518", d.fromUnix(1432011600 /* May 19th,2015 1AM EDST */, agency.getTimeZone()));

            org.junit.Assert.assertEquals("service doesn't run on may 19th", 0, agency.isServiceExceptionNow("CR-Weekday-Recovery-Providence-Dec13", 1432048600 /* May 19th,2015 11AM EDST */ ));
            
            org.junit.Assert.assertEquals("service should be removed may 25th", 2, agency.isServiceExceptionNow("CR-Weekday-Greenbush-Dec13", 1432555200 /* May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("service should be added may 25th", 1, agency.isServiceExceptionNow("CR-Sunday-Fitchburg-Aug14", 1432555200 /* May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("service not found in calendar.txt at all should return false", false, agency.isServiceRunningNormallyNow("CR-Sunday-Fitchburg-Aug14", 1432555200 /* May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("service is found in calendar.txt so should return false", false, agency.isServiceRunningNormallyNow("CR-Sunday-Fitchburg-Aug14", 1427889600 /* Wed. April 1,2015 12PM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("service is running Sunday calendar.txt so should return true", true, agency.isServiceRunningNormallyNow("CR-Sunday-Fitchburg-Aug14", 1427630400 /* 1427630400. March 29,2015 12PM EDST, memorial day */ ));

            // CR-Weekday-Worcester-Mar14 is a weekday train. valid from 20150330 - 20150619

            org.junit.Assert.assertEquals("Service is normally running on Memorial day, so this should return true", true, agency.isServiceRunningNormallyNow("CR-Weekday-Worcester-Mar14", 1432555200 /* Monday, May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("Service is removed on Memorial day, so this should return false", false, agency.isServiceRunningNow("CR-Weekday-Worcester-Mar14", 1432555200 /* Monday, May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("CR-Sunday-Haverhill-May14 running on mem day", true, agency.isServiceRunningNow("CR-Sunday-Haverhill-May14", 1432555200 /* Monday, May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("CR-Sunday-Haverhill-May14 running on mem day", false, agency.isServiceRunningNow("CR-Weekday-Haverhill-May14", 1432555200 /* Monday, May 25th,2015 8AM EDST, memorial day */ ));

            org.junit.Assert.assertEquals("straightforward query should just work", "CR-Fitchburg-CR-Weekday-Recovery-Fitchburg-Aug14-404", agency.getTripIdFromServiceAndShortName("CR-Weekday-Recovery-Fitchburg-Aug14", "404"));

            org.junit.Assert.assertEquals("lat/long be inside boundaries",true, JTS.isPointInBoundary(42.37883631647602, -71.8121337890625, agency.getBoundaries()));

            org.junit.Assert.assertEquals("lat/long should not be inside boundaries",false, JTS.isPointInBoundary(42.45588764197166, -74.7454833984375, agency.getBoundaries()));


            String[] sorted_stop_ids =  agency.getClosestStopIds(null, 42.494107, -71.520105, 2);

            org.junit.Assert.assertEquals("closest stop should be Littleton / Rte 495", "Littleton / Rte 495", sorted_stop_ids[0]);
            org.junit.Assert.assertEquals("second closest stop should be South Acton", "South Acton", sorted_stop_ids[1]);
            org.junit.Assert.assertEquals("furthest stop should be Southstation", "Wickford Junction", sorted_stop_ids[sorted_stop_ids.length -1]);


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