package lu.ing.gameofcode;

/**
 * Created by patrice on 09.04.16.
 */
public class GeoJsonItemPlace extends GeoJsonItem {

    private long longitude;
    private long latitude;
    private long altitude;

    public GeoJsonItemPlace() {
    }

    public GeoJsonItemPlace(String name, long longitude, long latitude) {
        super(name);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getAltitude() {
        return altitude;
    }

    public void setAltitude(long altitude) {
        this.altitude = altitude;
    }
}
