package com.iqtransit.gtfs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.IOException;
import com.iqtransit.*;
import com.iqtransit.agency.*;
import java.util.Date;


public class PredictionQuery {

		private AgencyInterface agency;
		private String last_prediction;

		public PredictionQuery(AgencyInterface agency) {
			this.agency = agency;
		}
	
		public String toString() {
			return last_prediction;
		}

		public boolean store() {
			// are there multiple ways you can store besides database? Log file , cloud?
			return true;
		}

		public void fetchPrediction(String line, String format, Date d) {
			
			// could be from a database, URL, local xml, json, cloud service. could also be historical for test cases.

	    	try {
		        CloseableHttpClient httpclient = HttpClients.createDefault();
		        HttpGet httpGet = new HttpGet(this.agency.getPredictionURL(line, format));
		        CloseableHttpResponse response1 = httpclient.execute(httpGet);
		        // The underlying HTTP connection is still held by the response object
		        // to allow the response content to be streamed directly from the network socket.
		        // In order to ensure correct deallocation of system resources
		        // the user MUST call CloseableHttpResponse#close() from a finally clause.
		        // Please note that if response content is not fully consumed the underlying
		        // connection cannot be safely re-used and will be shut down and discarded
		        // by the connection manager. 
		        try {
		            System.out.println(response1.getStatusLine());
		            HttpEntity entity1 = response1.getEntity();
		            // do something useful with the response body
		            // and ensure it is fully consumed
		            //EntityUtils.consume(entity1);
		            String entityContents = EntityUtils.toString(entity1);
		            this.last_prediction = entityContents;

		        } finally {
		            response1.close();
		        }
	    		
	    	} catch ( IOException e ) {
	    		System.out.println("Error fetching prediction from URL " + e.toString());
	    	}
	    	
		}

}