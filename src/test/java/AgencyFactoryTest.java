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
public class AgencyFactoryTest {


    @Test
    public void confirmFactory() {

        AgencyFactory af = new AgencyFactory();
        AgencyInterface generic_agency = af.createAgency("MBTA");
        org.junit.Assert.assertEquals("should match id you actually want", "MBTA", generic_agency.getId());
        AgencyInterface generic_agency2 = af.createAgency("METRA");
        org.junit.Assert.assertEquals("should match id you actually want", "METRA", generic_agency2.getId());
    }

}