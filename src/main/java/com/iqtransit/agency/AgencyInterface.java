package com.iqtransit.agency;

public interface AgencyInterface {

	abstract String[] getLines();
	abstract double[][] getLinePaths(String lineid); 
	abstract double[] getReducedLinePath(String lineid);
	abstract double[] getClosedPoly(String lineid);
	abstract String getPredictionURL(String lineid, String format);
}