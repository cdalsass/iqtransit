package com.iqtransit.agency;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;

import java.sql.Connection;

/* keep this in a single class so it's easily imported dynamically. */
public class MBTAPredeparture {

	public String csv; 

	public MBTAPredeparture(String url) {
		this.url = url;
	}

	public MBTAPredeparture() {
	
	}

	public String getCSV() {
		return csv;
	}

	protected String url = "http://developer.mbta.com/lib/gtrtfs/Departures.csv";

	public void fetch() {

	    try {

	    	// http://www.mkyong.com/java/apache-httpclient-examples/
	    	// http://hc.apache.org/httpcomponents-client-ga/quickstart.html

	        CloseableHttpClient httpclient = HttpClients.createDefault();
	        
	        HttpGet httpGet = new HttpGet(this.url);

	        CloseableHttpResponse response = httpclient.execute(httpGet);
	      
	        try {
	            
	            HttpEntity entity1 = response.getEntity();
	           
	           	BufferedReader rd = new BufferedReader(
				new InputStreamReader(entity1.getContent()));
			 
				
				String line = "";
				while ((line = rd.readLine()) != null) {
					csv += line;
				}

	           	EntityUtils.consume(entity1);

			} catch (Exception e) {
				System.out.println("Error reading from http response " + e.toString());
	        } finally {
	            response.close();
	        }
    		
    	} catch ( IOException e ) {
    		System.out.println("Error fetching pre-departure data from URL " + e.toString());
    	}
	}

	public void parse() throws IOException {
		CSVParser parser = CSVParser.parse(this.csv,CSVFormat.DEFAULT);
	}

	public void store(Connection conn) {
	}
}