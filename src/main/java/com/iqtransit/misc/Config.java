package com.iqtransit.misc;
import java.lang.Class;
import java.net.URL; 
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ClassLoader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

public class Config {

	private static Properties properties;


	public static Properties getProperties() {
		return properties;
	}

	public static Properties load() throws IOException {
		return loadInner("/config.properties", true);
	}

	public static Properties load(String filename) throws IOException {
		if (filename != null) {
			return loadInner(filename, false);
		} else {
			return loadInner("/config.properties", true);
		}
	}
		

	private static Properties loadInner(String filename, boolean is_resource) throws IOException {

		InputStream input = null;
		Properties prop = null;

		try {

			prop = new Properties();

			if (is_resource) {
	       		
	       		input = Config.class.getResourceAsStream(filename);

			} else {
				input = new FileInputStream(new File(filename));
			}

			if (input == null) {
       			throw new FileNotFoundException("'" + filename + "' not found.");
       		} 

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