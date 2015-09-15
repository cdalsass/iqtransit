package com.iqtransit.agency;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.ArrayList; 

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
public class AgenciesTest {


    @Test
    public void test() {

        Agencies agencies = new Agencies();
        ArrayList<AgencyInterface> list = agencies.withinBounds(42.495227, -71.4503558);
        org.junit.Assert.assertEquals("should be only 1", 1, list.size());
        AgencyInterface mbta = list.get(0);
        org.junit.Assert.assertEquals("should match id you actually want", "MBTA", mbta.getId());
        
    }

}