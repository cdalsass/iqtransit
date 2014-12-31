package com.iqtransit.server;
import java.util.ArrayList;
import java.util.List;
import java.util.Dictionary;
import java.util.concurrent.ConcurrentHashMap;
import com.iqtransit.geo.Locatable;

/* the purpose of this class is to manage location updates, eliminating dupes, clearing out old entries */

public class LocatableList {

	private ConcurrentHashMap<String,Locatable> locations;

	public LocatableList() {
		locations = new ConcurrentHashMap();
	}

	public int size() {
		return locations.size();
	}

	public void add(Locatable a) {
		locations.put(a.id, a);
	}

	public Locatable get(String id) {
		return locations.get(id);
	}

	public void update(Locatable newlocation) {
		Locatable existing = locations.get(newlocation.id);
		if (existing != null) {
			if (existing.latitude != newlocation.latitude || existing.longitude != newlocation.longitude ) {
				// something changed. update 
				existing.latitude = newlocation.latitude;
				existing.longitude = newlocation.longitude;
				// then notify any listeners. 
			}
		}
	}

	public void locationUpdate(String id, double latitude, double longitude) {
		Locatable a = new Locatable(id, latitude, longitude);
		// if locatable is present based on dictionary, add, otherwise update.

		if (locations.get(id) != null) { // if found, update. Otherwise, throw exception. 
			this.update(a);
		} else {
			this.add(a);	
		}		
	}

	public Locatable[] getLocations() {
		return new Locatable[0];
	}

}