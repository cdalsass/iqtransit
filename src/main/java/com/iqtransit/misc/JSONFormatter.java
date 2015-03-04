package com.iqtransit.misc;
import com.google.gson.Gson;
import java.lang.Class;

public class JSONFormatter  {
	
    public Object parse(String json, java.lang.Class myclass) {
        Gson gson = new Gson();
        try {
            Object result = gson.fromJson(json, myclass);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String format(Object a) {
    	Gson gson = new Gson();
		String json = gson.toJson(a);
    	return json;
    }

}