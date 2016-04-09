package lu.ing.gameofcode;

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

/**
 * Created by patrice on 09.04.16.
 */
public class BusDataGenerator {

    public static Pattern BUS_CODE_PATTERN = Pattern.compile("Ligne-([^ ]+)");

    private List<GeoJsonData> geoJsonData;
    private BusData busData = new BusData();

    public static void main(String... args) throws IOException {
        System.out.println("------------------------------------------");
        System.out.println("- Read data from http://opendata.vdl.lu");
        System.out.println("------------------------------------------");
        GeoJsonParser parser = new GeoJsonParser();
        List<GeoJsonData> jsonData = parser.readData();
        System.out.println("------------------------------------------");
        System.out.println("- Generate bus data");
        System.out.println("------------------------------------------");
        BusDataGenerator generator = new BusDataGenerator(jsonData);
        for (GeoJsonData geoJsonData : jsonData) {
            // Keep only bus lines for now
            if (geoJsonData.getType() == GeoJsonDataType.BUS_LINE) {
                generator.busData.addLine(generator.interpretBusLine(geoJsonData));
                break; // FIXME
            }
        }
        System.out.println("------------------------------------------");
    }

    public BusDataGenerator(List<GeoJsonData> geoJsonData) {
        this.geoJsonData = geoJsonData;
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
        for (GeoJsonItem item : geoJsonData.getItems()) {
            if (item instanceof GeoJsonItemPlace) {
                places.add((GeoJsonItemPlace) item);
            } else if (item instanceof GeoJsonItemPath) {
                if (((GeoJsonItemPath) item).getPoints().size() > 1) {
                    paths.add((GeoJsonItemPath) item);
                }
            }
        }

        List<GeoJsonItemPath> way1 = extractAndSortOneWay(paths);
        if (paths.isEmpty()) {
            System.err.println("Paths empty after one way: " + busLine.getName());
        }
        System.out.println("Way1 _______________");
        for (GeoJsonItemPath path : way1) {
            System.out.println(path);
        }

        List<GeoJsonItemPath> way2 = extractAndSortOneWay(paths);
        if (!paths.isEmpty()) {
            System.err.println("Paths not empty after two ways: " + busLine.getName());
        } else {
            System.out.println("All ok: " + busLine.getName());
        }
        System.out.println("Way2 _______________");
        for (GeoJsonItemPath path : way2) {
            System.out.println(path);
        }

        System.out.println("Remaining _______________");
        for (GeoJsonItemPath path : paths) {
            System.out.println(path);
        }

        return busLine;
    }

    List<GeoJsonItemPath> extractAndSortOneWay(Deque<GeoJsonItemPath> paths) {
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
                if (firstPoint.equals(path.getPoints().getLast())) {
                    extracted.addFirst(path);
                    iter.remove();
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
                if (lastPoint.equals(path.getPoints().getFirst())) {
                    extracted.addLast(path);
                    iter.remove();
                    found = true;
                }
            }
        } while(found);

        return extracted;
    }
}
