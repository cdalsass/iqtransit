package com.iqtransit.common;
import java.io.IOException;
import java.util.ArrayList;

public abstract class RealtimeResult {

	protected RealtimeSource source;

	public RealtimeResult(RealtimeSource rts) {
		source = rts;
	}
	
	public abstract ArrayList<RealtimeEntity> parse();

	public abstract String dump(byte [] b);
}