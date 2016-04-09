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

        for (BusPath path : busData.getPaths()) {
            String name = path.getParentBusStop().getName();
            if ("Luxexpo Entrée Sud Quai 3".equals(name)) {
                path.setLatitude(6.170159);
                path.setLongitude(49.634316);
            } else if ("Poutty Stein".equals(name)) {
                path.setLatitude(6.168955);
                path.setLongitude(49.635584);
            } else if ("Mathias Tresch".equals(name)) {
                path.setLatitude(6.16917);
                path.setLongitude(49.637525);
            } else if ("Léon Thyes".equals(name)) {
                path.setLatitude(6.165017);
                path.setLongitude(49.637292);
            } else if ("Avalon Quai 2".equals(name)) {
                path.setLatitude(6.161368);
                path.setLongitude(49.632635);
            } else if ("Konrad Adenauer".equals(name)) {
                path.setLatitude(6.157332);
                path.setLongitude(49.629615);
            } else if ("Antoine de St Exupéry".equals(name)) {
                path.setLatitude(6.151489);
                path.setLongitude(49.627179);
            } else if ("Jean Monnet".equals(name)) {
                path.setLatitude(6.145385);
                path.setLongitude(49.62477);
            } else if ("B.E.I.".equals(name)) {
                path.setLatitude(6.13885);
                path.setLongitude(49.620077);
            } else if ("Fondation Pescatore Quai 2".equals(name)) {
                path.setLatitude(6.126768);
                path.setLongitude(49.615664);
            } else if ("Hamilius Quai 2".equals(name)) {
                path.setLatitude(6.126049);
                path.setLongitude(49.610971);
            } else if ("Martyrs Quai 2".equals(name)) {
                path.setLatitude(6.128512);
                path.setLongitude(49.60654);
            } else if ("Paris / Zitha Quai 3".equals(name)) {
                path.setLatitude(6.130778);
                path.setLongitude(49.603843);
            } else if ("Gare Centrale Quai 102".equals(name)) {
                path.setLatitude(6.133205);
                path.setLongitude(49.600814);
            } else if ("Alsace".equals(name)) {
                path.setLatitude(6.131119);
                path.setLongitude(49.596472);
            } else if ("Lascombes".equals(name)) {
                path.setLatitude(6.125519);
                path.setLongitude(49.59426);
            } else if ("Hollerich, Gare".equals(name)) {
                path.setLatitude(6.120988);
                path.setLongitude(49.595141);
            } else if ("Gaasperecherbierg".equals(name)) {
                path.setLatitude(6.116988);
                path.setLongitude(49.593038);
            } else if ("Plantin".equals(name)) {
                path.setLatitude(6.116422);
                path.setLongitude(49.589739);
            } else if ("Cloche d'Or".equals(name)) {
                path.setLatitude(6.115334);
                path.setLongitude(49.583446);
            } else if ("Scharfen Eck".equals(name)) {
                path.setLatitude(6.112988);
                path.setLongitude(49.579446);
            } else if ("Kockelscheuer, Camping".equals(name)) {
                path.setLatitude(6.113213);
                path.setLongitude(49.573226);
            } else if ("Kockelscheuer, Patinoire".equals(name)) {
                path.setLatitude(6.108538);
                path.setLongitude(49.565369);
            } else if ("Kockelscheuer, Camping".equals(name)) {
                path.setLatitude(6.113213);
                path.setLongitude(49.573226);
            } else if ("Scharfen Eck".equals(name)) {
                path.setLatitude(6.112988);
                path.setLongitude(49.579446);
            } else if ("Cloche d'Or".equals(name)) {
                path.setLatitude(6.115334);
                path.setLongitude(49.583446);
            } else if ("Raiffeisen".equals(name)) {
                path.setLatitude(6.116907);
                path.setLongitude(49.586673);
            } else if ("Plantin".equals(name)) {
                path.setLatitude(6.116422);
                path.setLongitude(49.589739);
            } else if ("Gaasperecherbierg".equals(name)) {
                path.setLatitude(6.115338);
                path.setLongitude(49.583449);
            } else if ("Hollerich, Gare".equals(name)) {
                path.setLatitude(6.120988);
                path.setLongitude(49.595141);
            } else if ("Lascombes".equals(name)) {
                path.setLatitude(6.125519);
                path.setLongitude(49.59426);
            } else if ("Alsace".equals(name)) {
                path.setLatitude(6.131119);
                path.setLongitude(49.596472);
            } else if ("Gare Centrale Quai 1".equals(name)) {
                path.setLatitude(6.133034);
                path.setLongitude(49.599807);
            } else if ("Paris / Zitha Quai 1".equals(name)) {
                path.setLatitude(6.130778);
                path.setLongitude(49.603843);
            } else if ("Martyrs Quai 1".equals(name)) {
                path.setLatitude(6.126049);
                path.setLongitude(49.610971);
            } else if ("Hamilius Quai 1".equals(name)) {
                path.setLatitude(6.126049);
                path.setLongitude(49.610971);
            } else if ("Fondation Pescatore Quai 1".equals(name)) {
                path.setLatitude(6.126768);
                path.setLongitude(49.615664);
            } else if ("B.E.I.".equals(name)) {
                path.setLatitude(6.13885);
                path.setLongitude(49.620077);
            } else if ("Jean Monnet".equals(name)) {
                path.setLatitude(6.145385);
                path.setLongitude(49.62477);
            } else if ("Antoine de St Exupéry".equals(name)) {
                path.setLatitude(6.151489);
                path.setLongitude(49.627179);
            } else if ("Konrad Adenauer".equals(name)) {
                path.setLatitude(6.157332);
                path.setLongitude(49.629615);
            } else if ("Avalon Quai 1".equals(name)) {
                path.setLatitude(6.161368);
                path.setLongitude(49.632635);
            } else if ("Léon Thyes".equals(name)) {
                path.setLatitude(6.165017);
                path.setLongitude(49.637292);
            } else if ("Mathias Tresch".equals(name)) {
                path.setLatitude(6.16917);
                path.setLongitude(49.637525);
            } else if ("Poutty Stein".equals(name)) {
                path.setLatitude(6.168955);
                path.setLongitude(49.635584);
            }
        }
        return busData;
    }

    public void interpretBusLines() {
        for (GeoJsonData geoJsonData : geoJsonDataList) {
            // Keep only bus lines for now
            if (geoJsonData.getType() == GeoJsonDataType.BUS_LINE) {
                BusLine busLine = interpretBusLine(geoJsonData);
                convertUnits(busLine);
                busData.addLine(busLine);
                for (BusStop stop  : busLine.getWay()) {
                    busData.addPath(stop.getPath());
                }

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
