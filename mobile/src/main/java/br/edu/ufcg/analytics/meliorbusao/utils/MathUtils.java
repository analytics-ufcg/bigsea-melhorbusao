package br.edu.ufcg.analytics.meliorbusao.utils;

import android.graphics.PointF;

import br.edu.ufcg.analytics.meliorbusao.models.LocationData;

public class MathUtils {

    protected MathUtils() {}

    public static boolean pointIsInCircle(PointF pointForCheck, PointF center,
                                          double radius) {
        if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius)
            return true;
        else
            return false;
    }

    /**
     * Calcula a distancia entre 2 pontos
     * @param p1
     * @param p2
     * @return
     */
    public static double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.x - p1.x);
        double dLon = Math.toRadians(p2.y - p1.y);
        double lat1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.x);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    /**
     * Calcula a distancia entre 2 pontos de lat e long
     * @param latitudeOrig
     * @param longitudeOrig
     * @param latitudeDest
     * @param longitudeDest
     * @return
     */
    public static double getDistanceBetweenTwoPoints(double latitudeOrig, double longitudeOrig,
                                                     double latitudeDest, double longitudeDest) {
        PointF p1 = new PointF((float) latitudeOrig, (float) longitudeOrig);
        PointF p2 = new PointF((float) latitudeDest, (float) longitudeDest);

        return getDistanceBetweenTwoPoints(p1, p2);
    }

    public static double getDistanceBetweenTwoPoints(double latitudeOrig, double longitudeOrig,
                                                     LocationData dest) {
        PointF p1 = new PointF((float) latitudeOrig, (float) longitudeOrig);
        PointF p2 = new PointF((float) dest.getLatitude(), (float) dest.getLongitude());

        return getDistanceBetweenTwoPoints(p1, p2);
    }

    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point
     *            Point of origin
     * @param range
     *            Range in meters
     * @param bearing
     *            Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     */
    public static PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }
}
