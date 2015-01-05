package com.iqtransit.geo;
import com.iqtransit.geo.LocatableFormatter;
import com.google.gson.Gson;

public class LocatableJSONFormatter implements LocatableFormatter {
	
    public Locatable parse(String json) {
        Gson gson = new Gson();
        try {
            Locatable result = gson.fromJson(json, Locatable.class);
            System.out.println("OBJECT + " + result.toString());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String format(Locatable a) {
    	Gson gson = new Gson();
		String json = gson.toJson(a);
    	return json;
    }

}