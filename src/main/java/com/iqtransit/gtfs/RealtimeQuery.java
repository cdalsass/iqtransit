package com.iqtransit.gtfs;
import com.google.protobuf.CodedInputStream;

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
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iqtransit.agency.AgencyInterface;
import com.iqtransit.gtfs.GtfsRealtime.*;
import com.iqtransit.geo.Locatable;


public class RealtimeQuery {

		private class PredictionData {

			private boolean was_parsed;
			public byte[] bytes; 
			private String as_string;

			public PredictionData(byte[] bytes) {
				this.bytes = bytes;
				was_parsed = false;
				as_string = "";
			}
			public void setParsed(boolean parsed) {
				this.was_parsed = parsed; 
			}

		}

		private AgencyInterface agency;
		private PredictionData last_prediction; /* might be json or gtfs binary */
		 
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

		public ArrayList<Locatable> parse() {

			ArrayList<Locatable> results = new ArrayList<Locatable>();

	        CodedInputStream in = CodedInputStream.newInstance(this.last_prediction.bytes);
	        FeedMessage.Builder b = FeedMessage.newBuilder();
	        try {
	        	b.mergeFrom(in, null);
	        } catch (IOException e) {
	        	System.out.println("Error parsing GTFS realtime data");
	        }

	        FeedMessage feed = b.build();
	        List<FeedEntity>  entities = feed.getEntityList();
	        for (  FeedEntity entity : entities) {
	        	
	        	if (!entity.hasVehicle()) {
			      //continue;
			    }
			    System.out.println(" vehicle = " + entity.toString());
			    VehiclePosition vehicle = entity.getVehicle();
			    //Position.Builder position = Position.newBuilder();
			    Position position = vehicle.getPosition();
			    TripDescriptor trip = vehicle.getTrip();
			    VehicleDescriptor vehicle_for_id = vehicle.getVehicle();
			    Locatable locatable = new Locatable(vehicle_for_id.getId(), trip.getTripId(), position.getLatitude(), position.getLongitude(),position.getSpeed(),position.getBearing());
			    results.add(locatable);

	        }

	        this.last_prediction.was_parsed = true; 
	        return results; 
		}

		/*

        try {
            //GtfsRealtime rt = new GtfsRealtime();
            FeedMessage.Builder b = FeedMessage.newBuilder();
            FeedMessage feed = parseFeed("/tmp/TripUpdates.pb");
            List<FeedEntity>  entities = feed.getEntityList();
            org.junit.Assert.assertEquals("should be at least a few entities", true, entities.size() > 0 );
        } catch (Exception t) {
            org.junit.Assert.assertEquals("should not throw exception " + t, true, false);
            System.err.println(t);
        }
        */
        
        /* fetch predictions across the entire global universe of trains */

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