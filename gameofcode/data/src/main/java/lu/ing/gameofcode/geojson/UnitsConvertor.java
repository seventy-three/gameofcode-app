package lu.ing.gameofcode.geojson;

/**
 * Created by patrice on 09.04.16.
 */
public class UnitsConvertor {


    public static long luxLatitudeToGps(long luxLatitude) {
        return luxLatitude / 10000000000L;
    }

    public static long luxLongitudeToGps(long luxLongitude) {
        return luxLongitude / 10000000000L;
    }

    public static long distanceLuxToMeters(long distanceLux) {
        return distanceLux / 10000000000L;
    }

    public static long distanceToTimeFoot(long distance) {
        return (long) (((double) distance) / 0.75d);
    }

    public static long distanceToTimeBike(long distance) {
        return (long) (((double) distance) / 8.3d);
    }
}
