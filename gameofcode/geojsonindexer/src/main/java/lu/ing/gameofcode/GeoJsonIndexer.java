package lu.ing.gameofcode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonIndexer {

    public static final String BASE_URL = "http://opendata.vdl.lu/odaweb/";
    public static final String URL_LIST = BASE_URL + "?describe=1";
    public static final String URL_ITEM = BASE_URL + "?cat=";

    public static void main(String... args) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String list = loadUrl(URL_LIST);

        Type geoJsonDataType = new TypeToken<List<GeoJsonData>>() {}.getType();
        gsonBuilder.registerTypeAdapter(geoJsonDataType, new GeoJsonDeserializer());

        Gson gson = gsonBuilder.create();
        List<GeoJsonData> items = gson.fromJson(list, geoJsonDataType);

        for (GeoJsonData item : items) {
            System.out.println("Loading \"" + item.name + "\"");
            String line = loadUrl(URL_ITEM + item.id);
            System.out.println(line);
            //break;
        }
    }

    private static class GeoJsonData {
        private String id;
        private String name;
        public GeoJsonData(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class GeoJsonDeserializer implements JsonDeserializer<List<GeoJsonData>> {
        public List<GeoJsonData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<GeoJsonData> items = new ArrayList<>();
            for (JsonElement data : ((JsonObject) json).get("data").getAsJsonArray()) {
                String id = ((JsonObject) data).get("id").getAsString();
                String name = ((JsonObject) ((JsonObject) ((JsonObject) data).get("i18n")).get("fr")).get("name").getAsString();
                items.add(new GeoJsonData(id, name));
            }
            return items;
        }
    }

    public static String loadUrl(String url) throws IOException {
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
        return response.toString();
    }
}
