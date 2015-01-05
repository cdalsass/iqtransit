package com.iqtransit.geo;
import com.iqtransit.geo.Locatable;


public interface LocatableFormatter {
	public Locatable parse(String a);
	public String format(Locatable a); 	
}