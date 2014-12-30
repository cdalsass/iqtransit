package com.iqtransit.server;
import java.util.ArrayList;
import java.util.List;
import java.util.Dictionary;
import java.util.Hashtable;
import com.iqtransit.geo.Locatable;

/* the purpose of this class is to manage location updates, eliminating dupes, clearing out old entries */

public class LocatableList {

	private List<Locatable> locations;
	private Dictionary<String,Integer> indexbyid;

	public LocatableList() {
		locations = new ArrayList<Locatable>();
		indexbyid = new Hashtable();
	}

	public int size() {
		return locations.size();
	}

	public void add(Locatable a) {
		indexbyid.put(a.getId(), locations.size());
		locations.add(a);
	}

	public void update(Locatable newlocation) {

	}

	public void locationUpdate(String id, double latitute, double longitude) {
		Locatable a = new Locatable(id, latitute, longitude);
		// if locatable is present based on dictionary, add, otherwise update.

		if (indexbyid.get(id) != null) { // if found, update. Otherwise, throw exception. 
			this.update(a);
		} else {
			this.add(a);	
		}		
	}

	public Locatable[] getLocations() {
		return new Locatable[0];
	}

}