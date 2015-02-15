package com.iqtransit.gtfs;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Connection;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import java.util.List;
import java.util.ArrayList;

public abstract class RealtimeResult {

	protected RealtimeSource source;

	public RealtimeResult(RealtimeSource rts) {
		source = rts;
	}

	public abstract ArrayList<RealtimeEntity> parse();

	public String dump(byte [] bytes) {
			CodedInputStream in = CodedInputStream.newInstance(bytes);
			FeedMessage.Builder b = FeedMessage.newBuilder();
			try {
	        	b.mergeFrom(in, null);
	        } catch (IOException e) {
	        	System.out.println("Error parsing GTFS realtime data");
	        }
	        FeedMessage feed = b.build();
	        List<FeedEntity>  entities = feed.getEntityList();
	        String result = "";
	        for (  FeedEntity entity : entities) {
	        	result += entity.toString();
	        };
	        return result;
		}
}