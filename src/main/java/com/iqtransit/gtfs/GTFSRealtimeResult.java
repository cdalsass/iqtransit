package com.iqtransit.gtfs;
import com.iqtransit.common.*;
import java.util.List;
import java.util.ArrayList;
import com.google.protobuf.CodedInputStream;
import com.iqtransit.gtfs.GtfsRealtime.*;
import java.io.IOException;

/* includes anthing common to all GTFSRT results */

public abstract class GTFSRealtimeResult extends RealtimeResult {

	public GTFSRealtimeResult(RealtimeSource rts) {
		super(rts);
	}


	public String dump(byte [] bytes) {

		CodedInputStream in = CodedInputStream.newInstance(bytes);
		FeedMessage.Builder b = FeedMessage.newBuilder();
		try {
        	b.mergeFrom(in, null);
        } catch (IOException e) {
        	System.out.println("Error parsing realtime data");
        }
        FeedMessage feed = b.build();
        List<FeedEntity>  entities = feed.getEntityList();
        String result = "";
        for (  FeedEntity entity : entities) {
        	result += entity.toString();
        };
        return result;

	}

	// noop implementation.
	public abstract ArrayList<RealtimeEntity> parse();
}