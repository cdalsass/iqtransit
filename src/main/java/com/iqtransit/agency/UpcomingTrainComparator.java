package com.iqtransit.agency;
import java.util.Comparator;
import java.util.TimeZone;
import com.iqtransit.gtfs.*;

public class UpcomingTrainComparator implements Comparator<UpcomingTrain> {

        private TimeZone tz; /* needs to be set manually */
        private String[] sorted_stop_ids;

        public UpcomingTrainComparator(TimeZone tz, String [] sorted_stop_ids) {
            this.tz = tz;
            this.sorted_stop_ids = sorted_stop_ids;
        }

        @Override
        public int compare(UpcomingTrain o1, UpcomingTrain o2) {
            
            int o1_arrival_ms = GtfsTime.toMs(o1.arrival_time);
            int o2_arrival_ms = GtfsTime.toMs(o2.arrival_time);
            int ms_from_midnight = GtfsTime.msFromMidnight(this.tz, System.currentTimeMillis());

            int compare_1 = Integer.compare(java.util.Arrays.asList(sorted_stop_ids).indexOf(o1.stop_id), java.util.Arrays.asList(sorted_stop_ids).indexOf(o2.stop_id));

            // compare first by closest stop then by time.
            if (compare_1 == 0) {
            
                return Integer.compare(o1_arrival_ms, o2_arrival_ms); 
            
            } else {

                return compare_1;

            }


        }
    }