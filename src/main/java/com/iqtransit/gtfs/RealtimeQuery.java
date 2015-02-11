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
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;


import com.iqtransit.agency.AgencyInterface;

import com.iqtransit.geo.Locatable;


public abstract class RealtimeQuery {

		protected class PredictionData {

			protected boolean was_parsed;
			public byte[] bytes; 
			protected String as_string;

			public PredictionData(byte[] bytes) {
				this.bytes = bytes;
				was_parsed = false;
				as_string = "";
			}
			public void setParsed(boolean parsed) {
				this.was_parsed = parsed; 
			}

		}

		// a realtime query must have a parse method. 
		public abstract ArrayList<RealtimeResult> parse();

		protected AgencyInterface agency;
		protected PredictionData last_prediction; /* might be json or gtfs binary */
		 
 		public byte[] getLoadedBytes() {
 			return last_prediction.bytes;
 		}

		public RealtimeQuery(AgencyInterface agency) {
			this.agency = agency;
		}
	
		public String toString() {
			if (last_prediction.was_parsed == true) {
				return last_prediction.as_string;
			} else {
				this.parse();
				return last_prediction.as_string;
			}
			//return new String(last_prediction);
		}

		
      

		public void fetchPrediction(String line, String format, Date d) {
			
			if (d == null) {
				// could be from a database, URL, local xml, json, cloud service. could also be historical for test cases.
				// this fetches a new prediction. 
		    	try {

			        CloseableHttpClient httpclient = HttpClients.createDefault();
			        HttpGet httpGet = new HttpGet(this.agency.RealtimeQuery(line, format));
			        CloseableHttpResponse response = httpclient.execute(httpGet);
			        // The underlying HTTP connection is still held by the response object
			        // to allow the response content to be streamed directly from the network socket.
			        // In order to ensure correct deallocation of system resources
			        // the user MUST call CloseableHttpResponse#close() from a finally clause.
			        // Please note that if response content is not fully consumed the underlying
			        // connection cannot be safely re-used and will be shut down and discarded
			        // by the connection manager. 
			        try {
			            
			            HttpEntity entity1 = response.getEntity();
			            // do something useful with the response body
			            // and ensure it is fully consumed
			            //EntityUtils.consume(entity1);
			           	ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    entity1.writeTo(baos);
				
					    last_prediction = new PredictionData(baos.toByteArray());

					} catch (Exception e) {
						System.out.println("Error reading from http response " + e.toString());
			        } finally {
			            response.close();
			        }
		    		
		    	} catch ( IOException e ) {
		    		System.out.println("Error fetching prediction from URL " + e.toString());
		    	}
			} else {
				// fetch an older prediction source... database?  
			}
		}
}