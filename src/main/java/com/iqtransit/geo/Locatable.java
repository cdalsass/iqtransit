package com.iqtransit.geo;

public class Locatable {
	private double latitude;
	private double longitude;
	private String id;

	public Locatable(String id, double latitute, double longitude) {
		this.latitude = latitute;
		this.longitude = longitude;
		this.id = id;
	}

	public double getLatitute() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getId() {
		return id;
	}
}