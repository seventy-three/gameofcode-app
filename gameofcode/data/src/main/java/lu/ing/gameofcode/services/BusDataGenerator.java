package lu.ing.gameofcode.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lu.ing.gameofcode.geojson.*;
import lu.ing.gameofcode.geojson.GeoJsonData;
import lu.ing.gameofcode.geojson.GeoJsonDataType;
import lu.ing.gameofcode.model.BusData;
import lu.ing.gameofcode.model.BusLine;
import lu.ing.gameofcode.model.BusPath;
import lu.ing.gameofcode.model.BusStop;

/**
 * Created by patrice on 09.04.16.
 */
public class BusDataGenerator {

    public static Pattern BUS_CODE_PATTERN = Pattern.compile("Ligne-([^ ]+) .*");
    public static Pattern BUS_STOP_PATTERN = Pattern.compile("Ligne autobus: ([0-9]+) / (.*)<br>Direction\\(s\\): (.*)");
    // Ligne autobus: 9 / Martyrs Quai 1<br>Direction(s): Cents-Waassertuerm

    private List<GeoJsonData> geoJsonDataList;
    private BusData busData = new BusData();

    public static void main(String... args) throws IOException {
        System.out.println("------------------------------------------");
        System.out.println("- Read data from http://opendata.vdl.lu");
        System.out.println("------------------------------------------");
        GeoJsonParser parser = new GeoJsonParser();
        List<GeoJsonData> jsonData = parser.readData(null);
        System.out.println("------------------------------------------");
        System.out.println("- Generate bus data");
        System.out.println("------------------------------------------");
        BusDataGenerator generator = new BusDataGenerator(jsonData);
        generator.interpretBusLines();


        System.out.println("------------------------------------------");
        for (BusLine line : generator.getBusData().getLines()) {
            if ("18".equals(line.getCode())) {
                for (BusStop stop : line.getWay()) {
                    System.out.println(stop);
                }
            }
        }
    }

    public BusDataGenerator(List<GeoJsonData> geoJsonDataList) {
        this.geoJsonDataList = geoJsonDataList;
    }

    public BusData getBusData() {
        return busData;
    }

    public void interpretBusLines() {
        for (GeoJsonData geoJsonData : geoJsonDataList) {
            // Keep only bus lines for now
            if (geoJsonData.getType() == GeoJsonDataType.BUS_LINE) {
                BusLine busLine = interpretBusLine(geoJsonData);
                convertUnits(busLine);
                busData.addLine(busLine);
            }
        }



    }

    public BusLine interpretBusLine(GeoJsonData geoJsonData) {
        BusLine busLine = new BusLine();
        busLine.setName(geoJsonData.getName());
        Matcher busCodeMatcher = BUS_CODE_PATTERN.matcher(busLine.getName());
        busLine.setCode(busCodeMatcher.matches() ? busCodeMatcher.group(1) : "???");

        // Interpret bus routes

        // Separate places and paths
        List<GeoJsonItemPlace> places = new ArrayList<>();
        Deque<GeoJsonItemPath> paths = new LinkedList<>();
        Deque<GeoJsonItemPath> stops = new LinkedList<>();
        for (GeoJsonItem item : geoJsonData.getItems()) {
            if (item instanceof GeoJsonItemPlace) {
                places.add((GeoJsonItemPlace) item);
            } else if (item instanceof GeoJsonItemPath) {
                if (((GeoJsonItemPath) item).getPoints().size() > 1) {
                    paths.add((GeoJsonItemPath) item);
                } else {
                    stops.add((GeoJsonItemPath) item);
                }
            }
        }

        List<GeoJsonItemPath> way1 = extractAndSortOneWay(paths);
        // TODO add both terminus
        /*
        System.out.println("Way1 _______________");
        for (GeoJsonItemPath path : way1) {
            System.out.println(path);
        }
        */

        if (paths.isEmpty()) {
            System.out.println("All ok: " + busLine.getName());
        } else {
            System.err.println("Paths not empty: " + busLine.getName());
        }

        String firstDirection = null;

        List<BusStop> busStops = new ArrayList<>();
        for (GeoJsonItemPath path : way1) {
            BusStop busStop = new BusStop(busLine);
            BusPath way1Path = new BusPath(busStop);
            way1Path.setDistance(path.getDistance());
            GeoJsonItemPath.GeoJsonItemPoint point = path.getPoints().getFirst();
            way1Path.setLatitude(point.getLatitude());
            way1Path.setLongitude(point.getLongitude());
            way1Path.setAltitude(point.getAltitude());
            busStop.setPath(way1Path);
            busStops.add(busStop);
            for (GeoJsonItemPath stop : stops) {
                if (point.equals(stop.getPoints().getFirst())) {
                    Matcher matcher = BUS_STOP_PATTERN.matcher(stop.getName());
                    if (matcher.matches()) {
                        busStop.setName(matcher.group(2));
                        String direction = matcher.group(3).trim();
                        String[] directions = direction.split("/");
                        if (directions.length > 1) {
                            direction = directions[0].trim();
                        }
                        if (firstDirection == null) {
                            firstDirection = direction;
                        }
                        busStop.setDirection(directions.length > 1 ? BusStop.Direction.BOTH : (direction.equals(firstDirection) ? BusStop.Direction.WAY1 : BusStop.Direction.WAY2));
                    } else {
                        busStop.setName(stop.getName());
                    }
                }
            }
        }
        busLine.setWay(busStops);

        return busLine;
    }

    private List<GeoJsonItemPath> extractAndSortOneWay(Deque<GeoJsonItemPath> paths) {
        LinkedList<GeoJsonItemPath> extracted = new LinkedList<>();

        GeoJsonItemPath place = paths.poll();
        extracted.add(place);

        boolean found;
        GeoJsonItemPath.GeoJsonItemPoint firstPoint = place.getPoints().getFirst();
        GeoJsonItemPath.GeoJsonItemPoint lastPoint = place.getPoints().getLast();

        // To the left
        do {
            found = false;
            Iterator<GeoJsonItemPath> iter = paths.iterator();
            while (!found && iter.hasNext()) {
                GeoJsonItemPath path = iter.next();
                GeoJsonItemPath.GeoJsonItemPoint point = path.getPoints().getLast();
                if (firstPoint.equals(point)) {
                    extracted.addFirst(path);
                    iter.remove();
                    firstPoint = path.getPoints().getFirst();
                    found = true;
                }
            }
        } while(found);

        // To the right
        do {
            found = false;
            Iterator<GeoJsonItemPath> iter = paths.iterator();
            while (!found && iter.hasNext()) {
                GeoJsonItemPath path = iter.next();
                GeoJsonItemPath.GeoJsonItemPoint point = path.getPoints().getFirst();
                if (lastPoint.equals(point)) {
                    extracted.addLast(path);
                    iter.remove();
                    lastPoint = path.getPoints().getLast();
                    found = true;
                }
            }
        } while(found);

        return extracted;
    }

    public void convertUnits(BusLine busLine) {
        for (BusStop busStop : busLine.getWay()) {
            BusPath path = busStop.getPath();
            path.setTimeBike(UnitsConvertor.distanceToTimeBike(path.getDistance()));
            path.setTimeFoot(UnitsConvertor.distanceToTimeFoot(path.getDistance()));
            path.setTimeBus(UnitsConvertor.distanceToTimeBus(path.getDistance()));
        }
    }
}
