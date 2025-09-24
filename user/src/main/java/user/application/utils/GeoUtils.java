package user.application.utils;


import java.util.List;

public class GeoUtils {

    // Calculate the center (average) of the zone polygon
    public static double[] getCenter(List<List<Double>> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            throw new IllegalArgumentException("Zone coordinates are missing.");
        }

        double totalLat = 0.0;
        double totalLng = 0.0;

        for (List<Double> point : coordinates) {
            totalLat += point.get(0); // Latitude
            totalLng += point.get(1); // Longitude
        }

        int size = coordinates.size();
        return new double[]{ totalLat / size, totalLng / size }; // [lat, lng]
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    public static boolean isPointInPolygon(double lat, double lng, List<List<Double>> polygon) {
        int n = polygon.size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i).get(0);
            double yi = polygon.get(i).get(1);
            double xj = polygon.get(j).get(0);
            double yj = polygon.get(j).get(1);

            boolean intersect = ((yi > lng) != (yj > lng)) &&
                    (lat < (xj - xi) * (lng - yi) / (yj - yi) + xi);
            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }
}
