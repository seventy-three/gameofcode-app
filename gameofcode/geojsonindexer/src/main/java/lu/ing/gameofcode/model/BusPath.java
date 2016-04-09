package lu.ing.gameofcode.model;

/**
 * Created by patrice on 09.04.16.
 */
public class BusPath {

    BusStop parentBusStop;
    boolean isWay1;
    long latitude;
    long longitude;
    int altitude;
    long distance;
    int calories;
    int timeBus;
    int timeFoot;
    int timeBike;

    public BusPath() {
    }

    public BusPath(boolean isWay1, BusStop parentBusStop) {
        this.parentBusStop = parentBusStop;
    }

    public boolean isWay1() {
        return isWay1;
    }

    public void setWay1(boolean way1) {
        isWay1 = way1;
    }

    public BusStop getParentBusStop() {
        return parentBusStop;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getTimeBus() {
        return timeBus;
    }

    public void setTimeBus(int timeBus) {
        this.timeBus = timeBus;
    }

    public int getTimeFoot() {
        return timeFoot;
    }

    public void setTimeFoot(int timeFoot) {
        this.timeFoot = timeFoot;
    }

    public int getTimeBike() {
        return timeBike;
    }

    public void setTimeBike(int timeBike) {
        this.timeBike = timeBike;
    }
}
