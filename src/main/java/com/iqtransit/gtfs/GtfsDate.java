package com.iqtransit.gtfs;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.iqtransit.gtfs.GtfsTime;

/* handles common date things like converting 20150525 to unix timestamp, taking into account Timezone.
	
 */

public class GtfsDate {


		public String toUnixSeconds(String gtfs_formatted_time) {
			return "";
		}

		public String fromUnix(long reference_time_in_seconds, TimeZone tz) {

			Calendar referenceTime = Calendar.getInstance(tz);

			// pull this in to use secondsFromMidnight function.
			long from_midnight = GtfsTime.msFromMidnight(tz,reference_time_in_seconds*1000);

			// here's the magic calculation: Assume 4AM is the time when all trains start. Therefore, if the time is 0-4, it's actually the day before. see https://groups.google.com/forum/#!topic/massdotdevelopers/KR53XTi8PFc
			// if a very late train (that runs close to 4AM) is very late, you'll get a bug. 
			// the alternative is to define a "service day", but then finding when a train is running means iterating through all possible services... just too much work and computation. 

			// get the previous day by subtracting 4 hour.
			if (from_midnight/3600000 < 4) {
				reference_time_in_seconds =  reference_time_in_seconds - 3600*4;
			} 

			referenceTime.setTimeInMillis(reference_time_in_seconds*1000);
			
			DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return sdf.format(new Date(referenceTime.getTimeInMillis()));
			
		}

}