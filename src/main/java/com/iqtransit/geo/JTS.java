package com.iqtransit.geo;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class JTS {

  public static boolean isPointInBoundary(double latitude, double longitude, double[] boundary) {

    final GeometryFactory gf = new GeometryFactory();

    final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
 
    for (int i = 0; i < boundary.length; i = i + 2 ) {
        System.out.println("Add point " + boundary[i] + " , " + boundary[i+1]);
        points.add(new Coordinate(boundary[i], boundary[i+1]));
    }

    // add the beginning point, so "points must form a closed linestring"
    points.add(new Coordinate(boundary[0], boundary[1]));

    final Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points
        .toArray(new Coordinate[points.size()])), gf), null);

    final Coordinate coord = new Coordinate(latitude, longitude);
    final Point point = gf.createPoint(coord);

    return point.within(polygon);

  }

}