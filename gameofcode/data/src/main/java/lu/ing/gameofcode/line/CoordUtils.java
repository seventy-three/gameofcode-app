package lu.ing.gameofcode.line;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Type;

import lu.ing.gameofcode.veloh.VelohStationBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by julien on 09/04/16.
 */
public class CoordUtils {

    private static final String END_POINT = "http://www.act.public.lu/fr/gps-reseaux/reseaux-geodesiques/trafo-coordonnees/index.php?d=1&z=528.03&o=1&s=Convertir";

    private Request buildRequest(final String x, final String y) {
        return new Request.Builder()
                .url(END_POINT+"&x="+x+"&y="+y)
                .build();
    }

    private LatLng getRealCoords(final String x, String y) throws Exception {

        Request request;
        Response response;
        ResponseBody body;
        Reader charStream;

        OkHttpClient client = new OkHttpClient();

        // prepare the request
        request = buildRequest(x,y);

        // Execute the request and retrieve the response.
        response = client.newCall(request).execute();
        // Deserialize HTTP response to concrete type.
        body = response.body();
        charStream = body.charStream();
        BufferedReader br = new BufferedReader(charStream);
        String cline;
        String latitude = "";
        String longitude = "";
        while ((cline = br.readLine()) != null) {
            if (cline.indexOf("<p class=\"legendlike\">EUREF-Geographic</p>")>-1){
                cline = br.readLine();
                cline = cline.substring(cline.indexOf("Longitude&nbsp;: ")+17);
                longitude = cline.substring(0,cline.indexOf("&"));
                cline = br.readLine();
                cline = cline.substring(cline.indexOf("Latitude&nbsp;: ")+16);
                latitude = cline.substring(0,cline.indexOf("&"));
                break;
            }
        }

System.out.println("latitude="+latitude+" longitude="+longitude);

       /* double lat_1 = Double.parseDouble(latitude);
        double lon_1 = Double.parseDouble(longitude);
        final LatLng coord = new LatLng(lat_1, lon_1);

        return coord;*/
        return null;
    }

    public static void main(String... args) throws Exception {
        CoordUtils bl = new CoordUtils();
        bl.getRealCoords("76015.48","71655.69");
    }
}
