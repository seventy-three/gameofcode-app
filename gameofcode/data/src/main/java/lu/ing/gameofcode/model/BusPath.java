package lu.ing.gameofcode.model;

/**
 * Created by patrice on 09.04.16.
 */
public class BusPath {

    BusStop parentBusStop;
    long latitude;
    long longitude;
    int altitude;
    long distance;
    int caloriesFoot;
    int caloriesBike;
    int timeBus;
    int timeFoot;
    int timeBike;

    public BusPath() {
    }

    public BusPath(BusStop parentBusStop) {
        this.parentBusStop = parentBusStop;
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

    public int getCaloriesFoot() {
        return caloriesFoot;
    }

    public void setCaloriesFoot(int caloriesFoot) {
        this.caloriesFoot = caloriesFoot;
    }

    public int getCaloriesBike() {
        return caloriesBike;
    }

    public void setCaloriesBike(int caloriesBike) {
        this.caloriesBike = caloriesBike;
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

    @Override
    public String toString() {
        return "BusPath{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", distance=" + distance +
                ", caloriesFoot=" + caloriesFoot +
                ", caloriesBike=" + caloriesBike +
                ", timeBus=" + timeBus +
                ", timeFoot=" + timeFoot +
                ", timeBike=" + timeBike +
                '}';
    }
}
