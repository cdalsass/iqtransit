package com.iqtransit.misc;
import java.lang.Class;
import java.net.URL; 
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ClassLoader;

public class Config {

	private static Properties properties;

	public static Properties getProperties() {
		return properties;
	}

	public static Properties load() throws IOException {

		InputStream input = null;
		Properties prop = null;

		try {

			prop = new Properties();

			String filename = "/config.properties";
	       	input = Config.class.getResourceAsStream(filename);
	       	if (input == null) {
	       		throw new FileNotFoundException("config not found");
	       	} 
	       	// just can't get the relative paths to work for now, using resources. just hardcoding for now.
	       	//input = new FileInputStream("/Users/cdalsass/dev/iqtransit/config.properties");

	        prop.load(input);

	 
	    } catch (IOException ex) {
	        //ex.printStackTrace();
	        throw ex;
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    return prop;
	}
}