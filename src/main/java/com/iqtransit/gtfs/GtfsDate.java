package com.iqtransit.gtfs;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/* handles common date things like converting 20150525 to unix timestamp, taking into account Timezone.
	
 */

public class GtfsDate {


		public String toUnixSeconds(String gtfs_formatted_time) {
			return "";
		}

		public String fromUnix(long reference_time_in_seconds, TimeZone tz) {
			Calendar referenceTime = Calendar.getInstance(tz);
			referenceTime.setTime(new Date(reference_time_in_seconds*1000));

			DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return sdf.format(new Date(referenceTime.getTimeInMillis()));
			
		}

}