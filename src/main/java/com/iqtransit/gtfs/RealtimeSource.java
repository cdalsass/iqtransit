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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import com.iqtransit.agency.AgencyInterface;

public abstract class RealtimeSource {

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

		protected AgencyInterface agency;
		protected PredictionData last_prediction; /* might be json or gtfs binary */
		 
 		public byte[] getLoadedBytes() {
 			return last_prediction.bytes;
 		}

		public RealtimeSource(AgencyInterface agency) {
			this.agency = agency;
		}
	
		public abstract String GetDownloadUrl(String line, String format);	
 
		public RealtimeResult fetchPrediction(String line, String format, Date d) {
			
			byte [] results = null; 
			if (d == null) {
				// could be from a database, URL, local xml, json, cloud service. could also be historical for test cases.
				// this fetches a new prediction. 
		    	try {

			        CloseableHttpClient httpclient = HttpClients.createDefault();
			        
			        HttpGet httpGet = new HttpGet(this.GetDownloadUrl(line, format));

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
						
						// factoring of what to do once you have bytes. 
						results = baos.toByteArray();
					    setBytes(results);

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

			return Result(); 
		}

		private void setBytes(byte [] bytes) {
			last_prediction = new PredictionData(bytes);
		}

		// this associates the corrolary results class with the source. 
		public RealtimeResult Result() {
			return this.Result();
		}

		public RealtimeResult  loadLocalFile(String filename, String format) throws IOException {	
    		Path path = Paths.get(filename);
    		setBytes(Files.readAllBytes(path));
    		return Result(); 
  		}

}