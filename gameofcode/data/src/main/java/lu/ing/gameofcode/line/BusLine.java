package lu.ing.gameofcode.line;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BusLine {

    private Context context;
    private static final Gson GSON = new Gson();

    public static final int DIST_MIN=150;
    public static final int DIST_MAX=500;

    /**
     * List of bus line at a specific stop.
     */
    private static final String ENDPOINT_DEPARTURE = "http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&format=json&time=07:00";

    /**
     * List of stop at proximity.
     */
    private static final String ENDPOINT_STOP = "http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&stationProxy=yes";

    /**
     * List of operators managed (separeted by comma)
     */
    private static final String OPERATORS = "AVL,RGT";

    /**
     * Constructeur.
     */
    public BusLine() {
    }

    /**
     * Constructeur.
     *
     * @param context
     */
    public BusLine(Context context) {
        this.context = context;
    }

    /**
     * Prepare the API request.
     *
     * @param dist distance
     * @param latitude y
     * @param longitude x
     * @return
     */
    private Request buildRequestStop(final int dist, final String latitude, final String longitude) {
        return new Request.Builder()
                .url(ENDPOINT_STOP+"&look_maxdist="+dist+"&look_y="+latitude+"&look_x="+longitude)
                .build();
    }

    /**
     * Prepare the API request.
     *
     * @param stopId identity of the stop
     * @return
     */
    private Request buildRequestDeparture(final String stopId) {
        System.out.println(ENDPOINT_DEPARTURE+"&"+stopId);/**/
        return new Request.Builder()
                .url(ENDPOINT_DEPARTURE+"&operators="+OPERATORS+"&"+stopId)
                .build();
    }

    /**
     * Return a list of stopId in the a perimeter.
     *
     * @param client instance of an OkHttpClient
     * @param latitude the latitude (y)
     * @param longitude the longitude (x)
     * @return list of stopID
     * @throws Exception
     */
    private List<String> getAvailableStopIdList(OkHttpClient client, final String latitude, final String longitude) throws Exception {
        int currentDist = DIST_MIN;
        Request request;
        Response response;
        ResponseBody body;
        Reader charStream;
        List<String> stopIdList = new ArrayList<>();
        do {
            // prepare the request
            request = buildRequestStop(currentDist, latitude, longitude);

            // Execute the request and retrieve the response.
            response = client.newCall(request).execute();

            // Deserialize HTTP response to concrete type.
            body = response.body();
            charStream = body.charStream();
            BufferedReader br = new BufferedReader(charStream);
            String cline;
            while ((cline = br.readLine()) != null) {
                if (null != cline && cline.trim().length() > 0) {
                    stopIdList.add(cline);
                }
            }

            // enlarge the perimeters
            currentDist += 50;
        }
        while((null==stopIdList || stopIdList.size()==0) && currentDist<DIST_MAX);

        return stopIdList;
    }

    /**
     * List of avaiable lines at proximity.
     *
     * @param latitude the latitude (y)
     * @param longitude the longitude (x)
     * @return
     * @throws Exception
     */
    public LineBean[] getAvailableLines(final String latitude, final String longitude) throws Exception {

        LineBean[] items = new LineBean[0];

        OkHttpClient client = new OkHttpClient();
        final String[] latitudes = latitude.split("\\.");
        final String[] longitudes = longitude.split("\\.");
        final String latitudeCleaned = latitudes[0] + latitudes[1].substring(0, 6);
        final String longitudeCleaned = longitudes[0] + longitudes[1].substring(0, 6);

        List<String> stopIdList = getAvailableStopIdList(client, latitudeCleaned, longitudeCleaned);
        if (stopIdList.size()>0) {

            Request request;
            Response response;
            ResponseBody body;
            Reader charStream;

            GsonBuilder gsonBuilder = new GsonBuilder();
            Type LineJsonDataType = new TypeToken<List<LineBean>>() {
            }.getType();
            gsonBuilder.registerTypeAdapter(LineJsonDataType, new LineJsonDeserializer());

            final Map<String, LineBean> mlines = new HashMap<>();
            for (final String stopId : stopIdList) {

                request = buildRequestDeparture(stopId);

                // Execute the request and retrieve the response.
                response = client.newCall(request).execute();

                // Deserialize HTTP response to concrete type.
                body = response.body();
                charStream = body.charStream();

                final Gson gson = gsonBuilder.create();
                final List<LineBean> lineList = gson.fromJson(charStream, LineJsonDataType);
                for (final LineBean line : lineList) {
                    mlines.put(line.getNum(), line);
                }
            }

            items = Arrays.copyOf(mlines.values().toArray(), mlines.size(), LineBean[].class);
        }

        return items;
    }

    /**
     * Custom Json deserializer.
     */
    private static class LineJsonDeserializer implements JsonDeserializer<List<LineBean>> {
        public List<LineBean> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<LineBean> items = new ArrayList<>();
            final Map<String,LineBean> lines = new HashMap<>();
            for (final JsonElement departure : ((JsonObject) json).get("Departure").getAsJsonArray()) {
                JsonObject product = (JsonObject)((JsonObject) departure).get("Product");
                final String num = product.get("line").getAsString();
                if (null==lines.get(num)){

                    final String name = product.get("name").getAsString();
                    final String operatorCode = product.get("operatorCode").getAsString();

                    final String stopid = ((JsonObject) departure).get("stopid").getAsString();
                    final String stopExtId = ((JsonObject) departure).get("stopExtId").getAsString();

                    final LineBean line = new LineBean();
                    line.setNum(num);
                    line.setName(name);
                    line.setOperatorCode(operatorCode);
                    line.setStopId(stopid);
                    line.setStopExtId(stopExtId);
                    final String[] token = stopid.split("@");
                    if (token.length>3){
                        line.setLongitude(token[2]);
                        line.setLatitude(token[3]);
                    }

                    lines.put(num,line);
                }
            }
            items.addAll(lines.values());
            return items;
        }
    }

    /**
     * for testing
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        BusLine bl = new BusLine();
        bl.getAvailableLines("49599457","6132893");
    }
}