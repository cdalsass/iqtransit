// javac -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar LocatableItemListTest.java
// java -cp lib/junit.jar:.:lib/hamcrest-core-1.3.jar:build/libs/iqtransit.jar  org.junit.runner.JUnitCore LocatableItemListTest

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.iqtransit.misc.Config;

import java.io.IOException;
import java.util.Properties; 

public class TestConfig {

    @Test
    public void testLoadConfig() throws IOException {
        
        try {
            Properties config = Config.load();
        } catch (Exception e) {
            org.junit.Assert.assertEquals("should load config", null , e.toString());
        }

        try {
            Properties config = Config.load("/Users/cdalsass/dev/rtp/config.properties");
        } catch (Exception e) {
            org.junit.Assert.assertEquals("should load external config", null , e.toString());
        }

    }

}