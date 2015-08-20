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
import java.util.Calendar;
import java.util.TimeZone;

public class TestGtfsTime {


@Test
    public void TestGtfsTime() {

        // few tests on the GtfsTime classes. 
        org.junit.Assert.assertEquals( 23760000, GtfsTime.toMs("06:36:00") );
        
        org.junit.Assert.assertEquals("06:36:00", GtfsTime.fromMs(23760000));

        org.junit.Assert.assertEquals("12:00:00", GtfsTime.fromMs(12*3600*1000));

        // test GtfsTime 
        // TimeZone.getTimeZone("America/Los_Angeles")
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        //

        Calendar new_york_calendar = Calendar.getInstance(tz);
        new_york_calendar.set(Calendar.HOUR_OF_DAY, 14);
        new_york_calendar.set(Calendar.MINUTE, 33);
        new_york_calendar.set(Calendar.SECOND, 00);
        new_york_calendar.set(Calendar.MILLISECOND, 0);

        System.out.println("charlie getTimeInMillis returned " + new_york_calendar.getTimeInMillis());
        org.junit.Assert.assertEquals(52380000, GtfsTime.msFromMidnight( tz, new_york_calendar.getTimeInMillis()));
        org.junit.Assert.assertEquals(52380000, GtfsTime.msFromMidnight( tz, 1365791580000L /* april 12, 2013 14:33 EDST - 2 years, and 1 month ago from today, should return same as today, right  ? */));

        org.junit.Assert.assertEquals("6:36am", GtfsTime.formatTime("06:36:00"));
        org.junit.Assert.assertEquals("12:36am", GtfsTime.formatTime("24:36:00"));
        org.junit.Assert.assertEquals("12:06am", GtfsTime.formatTime("24:06:00"));
        org.junit.Assert.assertEquals("12:00pm", GtfsTime.formatTime("12:00:00"));

        org.junit.Assert.assertEquals(true, GtfsTime.isUpcoming("24:06:00", 1365791580000L /* april 12, 2013 14:33 EDST */, tz));
        org.junit.Assert.assertEquals(false, GtfsTime.isUpcoming("10:06:00", 1365791580000L /* april 12, 2013 14:33 EDST */, tz));

        org.junit.Assert.assertEquals(true, GtfsTime.isInProgress("10:06:00", "20:00:00", 1365791580000L /* april 12, 2013 14:33 EDST */, tz));
        org.junit.Assert.assertEquals(false, GtfsTime.isInProgress("10:06:00", "11:05:00", 1365791580000L /* april 12, 2013 14:33 EDST */, tz));

    }


}