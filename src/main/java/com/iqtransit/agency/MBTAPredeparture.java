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
import org.apache.commons.csv.CSVRecord;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.io.FileReader;

import java.sql.Connection;

/* keep this in a single class so it's easily imported dynamically. */
public class MBTAPredeparture {

	public String csv = "";
	public CSVParser parser = null;

	public MBTAPredeparture(String url) {
		this.url = url;
		parser = null;
	}

	public MBTAPredeparture() {
		parser = null;
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
					csv += line + System.lineSeparator();
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

	public boolean parse() throws IOException {
		//System.out.println(this.csv);
		parser = CSVParser.parse(this.csv,CSVFormat.DEFAULT);
		return true;
	}

	public boolean loadLocalFile(String filename) throws IOException {	

    	try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        csv = sb.toString();
    	} catch (IOException e) {
    		throw (e);
    	}
    	return true; 
    }

    /*

    TimeStamp,Origin,Trip,Destination,ScheduledTime,Lateness,Track,Status

	1424111720,"North Station","1307","Lowell",1424113200,0,,"On Time"
	1424111720,"North Station","1107","Rockport",1424114400,0,,"On Time"

	CREATE TABLE `pre_departure` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp,
  `timestamp` timestamp,
  `origin` varchar(255) NOT NULL,
  `trip` varchar(255) NOT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `scheduledtime` datetime NOT NULL,
  `lateness` int(10) DEFAULT NULL,
  `track` int(10) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40075 DEFAULT CHARSET=utf8

    */

	public boolean store(Connection conn) throws Exception {

		if (csv == null) {
			throw new Exception("cannot call store before calling fetch");
		}
	
		if (parser == null) {
			throw new Exception("cannot call store before calling parse");
		}

		String query = "INSERT INTO pre_departure (id, created, timestamp, origin, trip, destination, scheduledtime, lateness, track, status) values (null, now(), ?,?,?,?,?,?,?,?)";

        PreparedStatement preparedStmt = conn.prepareStatement(query);
		int rows_completed = 0; /* keep track of number of rows completed. if every row fails, throw an exception, otherwise just skip particular rows that fail */
		int rows_attempted = 0;
		for (CSVRecord record : parser) {

	        rows_attempted++;

	        try {
	        	preparedStmt.setTimestamp (1, stringToTimestamp(record.get(0)) /* because it takes milliseconds */);
	        } catch (NumberFormatException e) {
	        	System.out.println("could't parse integer timestamp in pre-departure csv");
	        	continue;
	        }

	        preparedStmt.setString (2, record.get(1));
	        preparedStmt.setString (3, record.get(2));
	        preparedStmt.setString (4, record.get(3));

	        if ("".equals(record.get(4))) {
	        	
	        	preparedStmt.setNull(5,Types.NULL);

	        } else {

		        try {
		        	preparedStmt.setTimestamp (5, stringToTimestamp(record.get(4)) /* because it takes milliseconds */);
		        } catch (NumberFormatException e) {
		        	System.out.println("could't parse integer 2cnd timestamp in pre-departure csv");
		        	continue;
		        }
	        	
	        }

	        if ("".equals(record.get(5))) {
	        	
	        	preparedStmt.setNull(6,Types.NULL);

	        } else {
		        try {
		        	preparedStmt.setInt (6, stringToInt(record.get(5)));
		        } catch (NumberFormatException e) {
		        	System.out.println("could't parse integer track  '" + record.get(5) + "' in pre-departure csv");
		        	continue;
		        }
		    }

		    if ("".equals(record.get(6))) {
	        	
	        	preparedStmt.setNull(7,Types.NULL);

	        } else {

		        try {
		        	preparedStmt.setInt (7, stringToInt(record.get(6)));
		        } catch (NumberFormatException e) {
		        	System.out.println("could't parse integer status '" + record.get(6) + "' in pre-departure csv");
		        	continue;
		        }
		    }
	       
		    preparedStmt.setString (8, record.get(7));

	        rows_completed++;

	        preparedStmt.execute();
		 }

		 if (rows_completed == 0 && rows_attempted > 0) {
		 	throw new Exception("every row failed to parse. ");
		 }

		return true;
	}

	/* assumes string is in seconds */
	private Integer stringToInt(String s) throws NumberFormatException {
		
		Integer result = null;

        try {
        	result = Integer.parseInt(s);
        } catch (NumberFormatException e) {   	
        	throw e;
        }

		return result;
	} 

	/* assumes string is in seconds */
	private Timestamp stringToTimestamp(String s) throws NumberFormatException {
		
		Long timestamp = null;

        try {
        	timestamp = Long.parseLong(s);
        } catch (NumberFormatException e) {   	
        	throw e;
        }

		return new Timestamp(timestamp*1000);
	} 
}