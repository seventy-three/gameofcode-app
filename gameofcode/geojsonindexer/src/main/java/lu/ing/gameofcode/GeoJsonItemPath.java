package lu.ing.gameofcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrice on 09.04.16.
 */
public class GeoJsonItemPath extends GeoJsonItem {

    private List<GeoJsonItemPoint> points = new ArrayList<>();
    private long distance = 0;

    public GeoJsonItemPath() {
    }

    public GeoJsonItemPath(String name) {
        super(name);
    }

    public void addPoint(long longitude, long latitude) {
        points.add(new GeoJsonItemPoint(longitude, latitude));
    }

    public List<GeoJsonItemPoint> getPoints() {
        return points;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public class GeoJsonItemPoint {
        private long longitude;
        private long latitude;
        private long altitude;

        public GeoJsonItemPoint() {
        }

        public GeoJsonItemPoint(long longitude, long latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public long getLongitude() {
            return longitude;
        }

        public long getLatitude() {
            return latitude;
        }

        public long getAltitude() {
            return altitude;
        }

        public void setAltitude(long altitude) {
            this.altitude = altitude;
        }
    }
}
