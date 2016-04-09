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

    // &look_maxdist=300
    // &look_x=6132893&look_y=49599457

    private static final String ENDPOINT_DEPARTURE = "http://travelplanner.mobiliteit.lu/restproxy/performLocating?accessId=cdt&format=json&time=07:00";
    private static final String ENDPOINT_STOP = "http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&stationProxy=yes";

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
                .url(ENDPOINT_DEPARTURE+"&"+stopId)
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

    public List<LineBean> getAvailableLines(final String latitude, final String longitude) throws Exception {

        List<LineBean> items = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        List<String> stopIdList = getAvailableStopIdList(client, latitude, longitude);
        if (stopIdList.size()>0){

            Request request;
            Response response;
            ResponseBody body;
            Reader charStream;
            int currentDist = DIST_MIN;

            GsonBuilder gsonBuilder = new GsonBuilder();
            Type LineJsonDataType = new TypeToken<List<LineBean>>() {}.getType();
            gsonBuilder.registerTypeAdapter(LineJsonDataType, new LineJsonDeserializer());

            Map<String,LineBean> mlines = new HashMap<>();
            for (final String stopId : stopIdList){

                request = buildRequestDeparture(stopId);

                // Execute the request and retrieve the response.
                response = client.newCall(request).execute();

                // Deserialize HTTP response to concrete type.
                body = response.body();
                System.out.println(response.code());
                if (response.code()==404){
                    AssetFileDescriptor descriptor = context.getAssets().openFd("DepartureMock.json");
                    charStream = new FileReader(descriptor.getFileDescriptor());
                }
                else{
                    charStream = body.charStream();
                }
                BufferedReader br = new BufferedReader(charStream);
                String cline;
                while ((cline = br.readLine()) != null) {
                    System.out.println(cline);
                    Log.d("CACA", "getAvailableLines: "+cline);
                }

/*                final Gson gson = gsonBuilder.create();
                final List<LineBean> lineList = gson.fromJson(charStream, LineJsonDataType);
                for (final LineBean line:lineList){
                    mlines.put(line.getNum(),line);
                    System.out.println(line.getNum());
                }*/
            }
            items.addAll(mlines.values());
        }

        return items;
    }

    private static class LineJsonDeserializer implements JsonDeserializer<List<LineBean>> {
        public List<LineBean> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<LineBean> items = new ArrayList<>();
            final Map<String,LineBean> lines = new HashMap<>();
            for (final JsonElement departure : ((JsonObject) json).get("Departure").getAsJsonArray()) {
                JsonObject product = (JsonObject)((JsonObject) departure).get("Product");
                final String num = ((JsonObject) product.get("num")).getAsString();
                if (null==lines.get(num)){
                    final String name = ((JsonObject) product.get("name")).getAsString();
                    final String catIn = ((JsonObject) product.get("catIn")).getAsString();
                    final LineBean line = new LineBean();
                    line.setNum(num);
                    line.setName(name);
                    line.setCatIn(catIn);
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
    /*public static void main(String... args) throws Exception {
        BusLine bl = new BusLine();
        bl.getAvailableLines("49599457","6132893");
    }*/
}