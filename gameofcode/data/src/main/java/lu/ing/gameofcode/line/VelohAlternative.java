package lu.ing.gameofcode.line;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VelohAlternative {

    private static final String API_KEY = "9b84de9ed23056f22bb009f57972fa76bf656360";

    private Context context;
    private static final Gson GSON = new Gson();

    public static final int DIST_MAX=300;

    /**
     * Constructeur.
     */
    public VelohAlternative() {
    }

    /**
     * Constructeur.
     *
     * @param context
     */
    public VelohAlternative(Context context) {
        this.context = context;
    }

    public double distanceTo(final String latitude1, final String longitude1, final String latitude2, final String longitude2)
    {
        double lat_1 = Double.parseDouble(latitude1);
        double lon_1 = Double.parseDouble(longitude1);
        double lat_2 = Double.parseDouble(latitude2);
        double lon_2 = Double.parseDouble(longitude2);
        final LatLng ll1 = new LatLng(lat_1, lon_1);
        final LatLng ll2 = new LatLng(lat_2, lon_2);
        return SphericalUtil.computeDistanceBetween(ll1,ll2);
    }

    public VelohStationBean[] getAvailableVeloh(final String latitude, final String longitude) throws Exception {

        InputStream inputStream = context.getResources().getAssets().open("VelohStation.json");
        Reader reader = new InputStreamReader(inputStream);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Type VelohStationJsonDataType = new TypeToken<List<VelohStationBean>>() {
        }.getType();
        final Gson gson = gsonBuilder.create();
        final List<VelohStationBean> stationList = gson.fromJson(reader, VelohStationJsonDataType);
        final Map<String, VelohStationBean> mStations = new HashMap<>();
        for (final VelohStationBean station : stationList) {
            System.out.println(station.getName());
            mStations.put(station.getNumber(), station);
        }

        return Arrays.copyOf(mStations.values().toArray(), mStations.size(), VelohStationBean[].class);
    }

    /**
     * for testing
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        VelohAlternative bl = new VelohAlternative();
        System.out.println("dist="+bl.distanceTo("49.579393","6.112815","49.58043","6.11465"));
    }
}