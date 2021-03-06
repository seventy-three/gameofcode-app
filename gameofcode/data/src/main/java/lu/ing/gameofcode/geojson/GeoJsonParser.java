package lu.ing.gameofcode.geojson;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeoJsonParser {

    public static final boolean LOAD_FROM_WEB = false;
    public static final boolean LOAD_FROM_ASSET = true;
    public static final String BASE_URL = "http://opendata.vdl.lu/odaweb/";
    public static final String URL_LIST = BASE_URL + "?describe=1";
    public static final String URL_ITEM = BASE_URL + "?cat=";

    public List<GeoJsonData> readData(Context context) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String list = loadUrl(URL_LIST, context);

        Type geoJsonDataType = new TypeToken<List<GeoJsonData>>() {}.getType();
        gsonBuilder.registerTypeAdapter(geoJsonDataType, new GeoJsonDeserializer());
        Type geoJsonItemPathType = new TypeToken<List<GeoJsonItemPath>>() {}.getType();
        gsonBuilder.registerTypeAdapter(geoJsonItemPathType, new GeoJsonItemPathDeserializer());
        Type geoJsonItemPlaceType = new TypeToken<List<GeoJsonItem>>() {}.getType();
        gsonBuilder.registerTypeAdapter(geoJsonItemPlaceType, new GeoJsonItemPlaceDeserializer());

        Gson gson = gsonBuilder.create();
        List<GeoJsonData> geoDatas = gson.fromJson(list, geoJsonDataType);

        for (GeoJsonData geoData : geoDatas) {
            String itemName = geoData.getName();
//            System.out.print("Loading \"" + itemName + "\"... ");
            String itemData = loadUrl(URL_ITEM + geoData.getId(), context);

            switch (geoData.getType()) {
                case BUS_LINE:
                    geoData.setItems((List<? extends GeoJsonItem>) gson.fromJson(itemData, geoJsonItemPathType));
//                    System.out.println("Ok");
                    break;
                case BIKE_ROAD:
                    geoData.setItems((List<? extends GeoJsonItem>) gson.fromJson(itemData, geoJsonItemPathType));
//                    System.out.println("Ok");
                    break;
                case OUTSIDE_PARKING:
                case MOTO_PARKING:
                case REDUCED_MOBILITY_PARKING:
                case INSIDE_PARKING:
                case PARK_AND_RIDE:
                case BIKE_PARKING:
                case PARK_AND_BIKE:
                case FUNTAINS:
                    geoData.setItems((List<? extends GeoJsonItem>) gson.fromJson(itemData, geoJsonItemPlaceType));
//                    System.out.println("Ok (items=" + geoData.getItems().size() + ")");
                    break;
            }

            convertUnits(geoData.getItems());
        }

        return geoDatas;
    }

    private void computeDistance(GeoJsonItemPath path) {
        long distance = 0;
        double x = -1, y = -1;
        for (GeoJsonItemPath.GeoJsonItemPoint point : path.getPoints()) {
            if (x != - 1 && y != -1) {
                double segX = Math.abs(point.getLongitude() - x);
                double segY = Math.abs(point.getLatitude() - y);
                distance += Math.sqrt(segX * segX + segY * segY);
            }
            x = point.getLongitude();
            y = point.getLatitude();
        }
        path.setDistance(distance);
    }

    public void convertUnits(List<? extends GeoJsonItem> items) {
        for (GeoJsonItem item : items) {
            if (item instanceof GeoJsonItemPath) {
                GeoJsonItemPath path = GeoJsonItemPath.class.cast(item);
                path.setDistance(UnitsConvertor.distanceLuxToMeters(path.getDistance()));
                for (GeoJsonItemPath.GeoJsonItemPoint point : path.getPoints()) {
                    point.setLatitude(point.getLatitude());
                    point.setLongitude(point.getLongitude());
                }
            } else if (item instanceof GeoJsonItemPlace) {
                // TODO
            }
        }
    }

    public String loadUrl(String url, Context context) throws IOException {
        String result;
        String filename = url.substring(url.lastIndexOf("=") + 1) + ".json";
        File path = new File(filename);
        if (LOAD_FROM_WEB) {
            // Load from web
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "FitBus");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();

            // Save to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.append(result);
            writer.close();
            System.out.println("Write to file: " + path.getAbsolutePath());
        } else if (LOAD_FROM_ASSET) {
            InputStream inputStream = context.getResources().getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
            result = buffer.toString();
            reader.close();
//            System.out.println("Read from asset: " + path.getAbsolutePath());
        } else {
            // Load from file
            Scanner scanner = new Scanner(path);
            result = scanner.useDelimiter("\\A").next();
            scanner.close();
        }

        return result;
    }

    private static class GeoJsonItemPlaceDeserializer implements JsonDeserializer<List<GeoJsonItemPlace>> {
        public List<GeoJsonItemPlace> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<GeoJsonItemPlace> items = new ArrayList<>();
            for (JsonElement data : ((JsonObject) json).get("features").getAsJsonArray()) {
                String name = ((JsonObject) ((JsonObject) data).get("properties")).get("name").getAsString();
                JsonElement geometry = ((JsonObject) data).get("geometry");
                JsonElement pt = ((JsonObject) geometry).get("coordinates").getAsJsonArray();
                GeoJsonItemPlace path = new GeoJsonItemPlace(name,
                        100000 - ((JsonArray) pt).get(0).getAsDouble(),
                        80000 - ((JsonArray) pt).get(1).getAsDouble());
                items.add(path);
            }
            return items;
        }
    }

    private class GeoJsonItemPathDeserializer implements JsonDeserializer<List<GeoJsonItemPath>> {
        public List<GeoJsonItemPath> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<GeoJsonItemPath> items = new ArrayList<>();
            for (JsonElement data : ((JsonObject) json).get("features").getAsJsonArray()) {
                String name = ((JsonObject) ((JsonObject) data).get("properties")).get("name").getAsString();
                GeoJsonItemPath path = new GeoJsonItemPath(name);
                items.add(path);

                JsonElement geometry = ((JsonObject) data).get("geometry");
                String type = ((JsonObject) geometry).get("type").getAsString();
                switch (type) {
                    case "LineString":
                        for (JsonElement pt : ((JsonObject) geometry).get("coordinates").getAsJsonArray()) {
                            path.addPoint(100000 - ((JsonArray) pt).get(0).getAsDouble(),
                                    80000 - (((JsonArray) pt).get(1).getAsDouble()));
                        }
                        computeDistance(path);
                        break;
                    case "Point":
                        JsonElement pt = ((JsonObject) geometry).get("coordinates").getAsJsonArray();
                        path.addPoint(100000 - ((JsonArray) pt).get(0).getAsDouble(),
                                80000 - (((JsonArray) pt).get(1).getAsDouble()));
                        break;
                }
            }
            return items;
        }
    }

    private class GeoJsonDeserializer implements JsonDeserializer<List<lu.ing.gameofcode.geojson.GeoJsonData>> {
        public List<lu.ing.gameofcode.geojson.GeoJsonData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<lu.ing.gameofcode.geojson.GeoJsonData> items = new ArrayList<>();
            for (JsonElement data : ((JsonObject) json).get("data").getAsJsonArray()) {
                String id = ((JsonObject) data).get("id").getAsString();
                String name = ((JsonObject) ((JsonObject) ((JsonObject) data).get("i18n")).get("fr")).get("name").getAsString();
                GeoJsonDataType type = null;
                if (name.startsWith("Ligne")) {
                    type = GeoJsonDataType.BUS_LINE;
                } else if (name.equals("Zones eaux")) {
                    type = null;
                } else if (name.equals("Point de location vel'oH!")) {
                    type = GeoJsonDataType.BIKE_ROAD;
                    name = "Pistes cyclables"; // !! Fix label from source data !!
                } else if ("Parking en surface".equals(name)) {
                    type = GeoJsonDataType.OUTSIDE_PARKING;
                } else if ("Emplacements pour motos".equals(name)) {
                    type = GeoJsonDataType.MOTO_PARKING;
                } else if ("Parking mobilité réduite".equals(name)) {
                    type = GeoJsonDataType.REDUCED_MOBILITY_PARKING;
                } else if ("Parking couvert".equals(name)) {
                    type = GeoJsonDataType.INSIDE_PARKING;
                } else if ("Park + Ride".equals(name)) {
                    type = GeoJsonDataType.PARK_AND_RIDE;
                } else if ("Emplacements pour vélos".equals(name)) {
                    type = GeoJsonDataType.BIKE_PARKING;
                } else if ("Park + Bike".equals(name)) {
                    type = GeoJsonDataType.PARK_AND_BIKE;
                } else if ("Fontaines".equals(name)) {
                    type = GeoJsonDataType.FUNTAINS;
                }
                if (type != null) {
                    items.add(new GeoJsonData(id, type, name));
                }
            }
            return items;
        }
    }

}
