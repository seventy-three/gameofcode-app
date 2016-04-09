package lu.ing.gameofcode.geojson;

/**
 * Created by patrice on 09.04.16.
 */
public class UnitsConvertor {

    public static double distanceLuxToMeters(double distanceLux) {
        return distanceLux;
    }

    public static double distanceToTimeFoot(double distance) {
        return distance / 0.75d;
    }

    public static double distanceToTimeBus(double distance) {
        return distance / 14d;
    }

    public static double distanceToTimeBike(double distance) {
        return distance / 8.3d;
    }
}
