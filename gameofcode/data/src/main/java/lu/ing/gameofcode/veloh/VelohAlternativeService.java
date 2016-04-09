package lu.ing.gameofcode.veloh;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class VelohAlternativeService {

    private static final String API_KEY = "9b84de9ed23056f22bb009f57972fa76bf656360";

    private static final String END_POINT = "https://api.jcdecaux.com/vls/v1/stations/%NUM%?contract=Luxembourg&apiKey="+API_KEY;

    private Context context;
    private static final Gson GSON = new Gson();

    public static final int DIST_MAX=300;

    /**
     * Constructeur.
     */
    public VelohAlternativeService() {
    }

    /**
     * Constructeur.
     *
     * @param context the context
     */
    public VelohAlternativeService(Context context) {
        this.context = context;
    }

    /**
     * Prepare the API request.
     *
     * @param num id of the station
     * @return
     */
    private Request buildRequest(final String num) {
        return new Request.Builder()
                .url(END_POINT.replaceAll("%NUM%",num))
                .build();
    }

    /**
     * Return a list of stopId in the a perimeter.
     *
     * @param client instance of an OkHttpClient
     * @param num id of the station
     * @return true id bike and stand are available, else false
     * @throws Exception
     */
    private boolean getAvailableBike(OkHttpClient client, final String num) throws Exception {

        boolean available = false;
        Request request;
        Response response;
        ResponseBody body;
        Reader charStream;

        // prepare the request
        request = buildRequest(num);

        // Execute the request and retrieve the response.
        response = client.newCall(request).execute();
        if (response.code()!=404) {

            // Deserialize HTTP response to concrete type.
            body = response.body();
            charStream = body.charStream();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Type VelohStationJsonDataType = new TypeToken<VelohStationBean>() {
            }.getType();
            final Gson gson = gsonBuilder.create();

            // station, bike and stand are available ?
            final VelohStationBean station = gson.fromJson(charStream, VelohStationJsonDataType);
            if (null!=station){
                System.out.println("station n°="+num+" status="+station.getStatus()+" available bike="+station.getAvailable_bikes()+" available stand="+station.getAvailable_bike_stands());
                available = ("OPEN".equals(station.getStatus()) &&
                        null!=station.getAvailable_bikes() && station.getAvailable_bikes()>0 &&
                        null!=station.getAvailable_bike_stands() && station.getAvailable_bike_stands()>0);
            }
        }
        return available;
    }

    /**
     * Compute the distance between two gps coords.
     * @param latitude1 latitude first coord
     * @param longitude1 longitude first coord
     * @param latitude2 latitude second coord
     * @param longitude2 longitude seconf coord
     * @return
     */
    public double distanceTo(final String latitude1, final String longitude1, final String latitude2, final String longitude2)
    {
        double lat_2 = Double.parseDouble(latitude2);
        double lon_2 = Double.parseDouble(longitude2);
        return distanceTo(latitude1,longitude1,lat_2,lon_2);
    }

    /**
     * Compute the distance between two gps coords.
     * @param latitude1 latitude first coord
     * @param longitude1 longitude first coord
     * @param lat_2 latitude second coord
     * @param lon_2 longitude second coord
     * @return
     */
    private double distanceTo(final String latitude1, final String longitude1, final double lat_2, final double lon_2)
    {
        double lat_1 = Double.parseDouble(latitude1);
        double lon_1 = Double.parseDouble(longitude1);
        final LatLng ll1 = new LatLng(lat_1, lon_1);
        final LatLng ll2 = new LatLng(lat_2, lon_2);
        return SphericalUtil.computeDistanceBetween(ll1,ll2);
    }

    /**
     * Return the list of available station of Veloh.
     * @param latitude latitude
     * @param longitude longitude
     * @return
     * @throws Exception
     */
    public VelohStationBean[] getAvailableVeloh(final String latitude, final String longitude) throws Exception {

        OkHttpClient client = new OkHttpClient();

        InputStream inputStream = context.getResources().getAssets().open("VelohStation.json");
        Reader reader = new InputStreamReader(inputStream);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Type VelohStationJsonDataType = new TypeToken<List<VelohStationBean>>() {
        }.getType();
        final Gson gson = gsonBuilder.create();
        final List<VelohStationBean> stationList = gson.fromJson(reader, VelohStationJsonDataType);
        final Map<String, VelohStationBean> mStations = new HashMap<>();
        for (final VelohStationBean station : stationList) {
            double dist = distanceTo(latitude,longitude,station.getLatitude(),station.getLongitude());
            if (dist<=DIST_MAX && getAvailableBike(client,station.getNumber())) {
                mStations.put(station.getNumber(), station);
            }
        }

        return Arrays.copyOf(mStations.values().toArray(), mStations.size(), VelohStationBean[].class);
    }

    /**
     * for testing
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        VelohAlternativeService bl = new VelohAlternativeService();
        System.out.println("dist="+bl.distanceTo("49.579393","6.112815","49.58043","6.11465"));
    }
}