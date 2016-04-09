package lu.ing.gameofcode.geojson;

/**
 * Created by patrice on 09.04.16.
 */
public class GeoJsonItemPlace extends GeoJsonItem {

    private double longitude;
    private double latitude;
    private long altitude;

    public GeoJsonItemPlace() {
    }

    public GeoJsonItemPlace(String name, double longitude, double latitude) {
        super(name);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getAltitude() {
        return altitude;
    }

    public void setAltitude(long altitude) {
        this.altitude = altitude;
    }
}
