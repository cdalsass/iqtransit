package com.iqtransit.agency;
import java.util.ArrayList; 
import com.iqtransit.geo.JTS;

public class Agencies {

        public ArrayList<AgencyInterface> withinBounds(double lat, double lon) {

            ArrayList<AgencyInterface> results = new ArrayList<AgencyInterface>();

            String[] agencies_available = AgencyFactory.available();
                

            for (int i = 0; i < agencies_available.length; i++) {
                AgencyInterface agency =  AgencyFactory.createAgency(agencies_available[i]);
                
                if (JTS.isPointInBoundary(lat, lon, agency.getBoundaries())) {
                    results.add(agency);
                }

            }

            return results; 
        }

}