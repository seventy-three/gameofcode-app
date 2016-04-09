package lu.ing.gameofcode.model;

/**
 * Created by patrice on 09.04.16.
 */
public class BusPath {

    BusStop parentBusStop;
    double latitude;
    double longitude;
    int altitude;
    double distance;
    double caloriesFoot;
    double caloriesBike;
    double timeBus;
    double timeFoot;
    double timeBike;

    public BusPath() {
    }

    public BusPath(BusStop parentBusStop) {
        this.parentBusStop = parentBusStop;
    }

    public BusStop getParentBusStop() {
        return parentBusStop;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCaloriesFoot() {
        return caloriesFoot;
    }

    public void setCaloriesFoot(double caloriesFoot) {
        this.caloriesFoot = caloriesFoot;
    }

    public double getCaloriesBike() {
        return caloriesBike;
    }

    public void setCaloriesBike(double caloriesBike) {
        this.caloriesBike = caloriesBike;
    }

    public double getTimeBus() {
        return timeBus;
    }

    public void setTimeBus(double timeBus) {
        this.timeBus = timeBus;
    }

    public double getTimeFoot() {
        return timeFoot;
    }

    public void setTimeFoot(double timeFoot) {
        this.timeFoot = timeFoot;
    }

    public double getTimeBike() {
        return timeBike;
    }

    public void setTimeBike(double timeBike) {
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
