package com.iqtransit.gtfs;
import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat; 

public class GtfsTime {
	
	public static int toMs(String mtime /* e.g. 14:19:00 */ ) throws NumberFormatException {

		int ONESECOND = 1000;
		int ONEMINUTE = ONESECOND*60;
		int ONEHOUR = ONEMINUTE*60;

		return Integer.parseInt(mtime.substring(0,2).replaceAll("/^0/",""))*ONEHOUR + Integer.parseInt(mtime.substring(3,5).replaceAll("/^0/",""))*ONEMINUTE;	
	}

	// go from 32,999,000 ms to "15:23:00"
	public static String fromMs(int ms_from_midnight) {

		int ONEMINUTE = 60;
		int ONEHOUR = ONEMINUTE*60;

		int seconds = ms_from_midnight/1000;
		int hours = seconds/ONEHOUR;
		int minutes = (seconds % ONEHOUR)/ONEMINUTE;

		return  String.format("%02d",hours) + ":" + String.format("%02d",minutes) + ":00"; 

	}

	/* return the number of milliseconds from midnight, given reference time and timezone */

	public static int msFromMidnight(TimeZone tz, long current_date_millis) {
		
		Calendar atMidnight = Calendar.getInstance(tz);
		
		// it may be possible that reference date is not today ... need to take this into account. 
		// for example, 4:00 AM should return same number of millis for any date - same TimeZone.  
		Calendar referenceTime = Calendar.getInstance(tz);
		referenceTime.setTimeInMillis(current_date_millis);

		atMidnight.set(Calendar.DAY_OF_MONTH, referenceTime.get(Calendar.DAY_OF_MONTH));
		atMidnight.set(Calendar.MONTH, referenceTime.get(Calendar.MONTH)); // -1 as month is zero-based
		atMidnight.set(Calendar.YEAR , referenceTime.get(Calendar.YEAR));
		
		atMidnight.set(Calendar.HOUR_OF_DAY, 0);
		atMidnight.set(Calendar.MINUTE, 0);
		atMidnight.set(Calendar.SECOND, 0);
		atMidnight.set(Calendar.MILLISECOND, 0);

		double difference =  (referenceTime.getTimeInMillis() - atMidnight.getTimeInMillis());
		return (int) difference;
	}


}