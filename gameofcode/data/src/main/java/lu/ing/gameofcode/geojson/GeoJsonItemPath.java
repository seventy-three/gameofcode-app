package lu.ing.gameofcode.geojson;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by patrice on 09.04.16.
 */
public class GeoJsonItemPath extends GeoJsonItem {

    private Deque<GeoJsonItemPoint> points = new LinkedList<>();
    private long distance = 0;
    private long elevationGain;
    private long verticalDrop;

    public GeoJsonItemPath() {
    }

    public GeoJsonItemPath(String name) {
        super(name);
    }

    public void addPoint(long longitude, long latitude) {
        points.add(new GeoJsonItemPoint(longitude, latitude));
    }

    public Deque<GeoJsonItemPoint> getPoints() {
        return points;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
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
        private long longitude;
        private long latitude;
        private int altitude;

        public GeoJsonItemPoint() {
        }

        public GeoJsonItemPoint(long longitude, long latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public void setLongitude(long longitude) {
            this.longitude = longitude;
        }

        public long getLongitude() {
            return longitude;
        }

        public void setLatitude(long latitude) {
            this.latitude = latitude;
        }

        public long getLatitude() {
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

            return longitude == that.longitude && latitude == that.latitude;

        }

        @Override
        public int hashCode() {
            int result = (int) (longitude ^ (longitude >>> 32));
            result = 31 * result + (int) (latitude ^ (latitude >>> 32));
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
