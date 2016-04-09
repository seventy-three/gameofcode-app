package lu.ing.gameofcode.geojson;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by patrice on 09.04.16.
 */
public class GeoJsonItemPath extends GeoJsonItem {

    private Deque<GeoJsonItemPoint> points = new LinkedList<>();
    private double distance = 0;
    private long elevationGain;
    private long verticalDrop;

    public GeoJsonItemPath() {
    }

    public GeoJsonItemPath(String name) {
        super(name);
    }

    public void addPoint(double longitude, double latitude) {
        points.add(new GeoJsonItemPoint(longitude, latitude));
    }

    public Deque<GeoJsonItemPoint> getPoints() {
        return points;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(long elevationGain) {
        this.elevationGain = elevationGain;
    }

    public long getVerticalDrop() {
        return verticalDrop;
    }

    public void setVerticalDrop(long verticalDrop) {
        this.verticalDrop = verticalDrop;
    }

    public class GeoJsonItemPoint {
        private double longitude;
        private double latitude;
        private int altitude;

        public GeoJsonItemPoint() {
        }

        public GeoJsonItemPoint(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public int getAltitude() {
            return altitude;
        }

        public void setAltitude(int altitude) {
            this.altitude = altitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GeoJsonItemPoint that = (GeoJsonItemPoint) o;

            return Double.compare(that.longitude, longitude) == 0 && Double.compare(that.latitude, latitude) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(longitude);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(latitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "[" + longitude +
                    ", " + latitude +
                    ']';
        }
    }

    @Override
    public String toString() {
        if (points == null || points.isEmpty()) {
            return "GeoJsonItemPath[EMPTY]";
        }
        return getName() +
                points.getFirst() +
                "->" +
                points.getLast() +
                ']';
    }
}
