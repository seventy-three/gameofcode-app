package lu.ing.gameofcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import lu.ing.gameofcode.line.BusLine;
import lu.ing.gameofcode.line.LineBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
        implements
            OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            LocationListener,
            GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private Location userLocation;
    private GoogleApiClient googleApiClient;
    private boolean selectingHome = true;
    private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);;

    @Bind(R.id.select_btn)
    Button selectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);

        // lignes disponibles du point de depart à l'arrivée
        final BusLine line = new BusLine(this);
        spiceManager.execute(new SpiceRequest<LineBean[]>(LineBean[].class) {
            @Override
            public LineBean[] loadDataFromNetwork() throws Exception {
                try {
                    final LineBean[] startList = line.getAvailableLines("49.599457","6.132893");
                    final LineBean[] endList = line.getAvailableLines("49.579455","6.112891");
                    List<LineBean> matchList = new ArrayList<>();
                    for (final LineBean lineS : startList){
                        if(null!=lineS.getNum()) {
                            for (final LineBean lineE : endList) {
                                if (lineS.getNum().equals(lineE.getNum())){
                                    matchList.add(lineE);
                                    break;
                                }
                            }
                        }
                    }
                    return Arrays.copyOf(matchList.toArray(), matchList.size(), LineBean[].class);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new RequestListener<LineBean[]>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("MAIN","getAvailableLines Failure");
            }
            @Override
            public void onRequestSuccess(LineBean[] lines) {
                Log.d("MAIN","getAvailableLines Success nb matched="+lines.length);
                for (final LineBean line : lines) {
                    Log.d("MAIN", "getAvailableLines LINE N°"+line.getNum());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, this);

    }

    private void updateGoogleMaps() {
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 14);
        googleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (userLocation != null) {
            return;
        }
        userLocation = location;
        updateGoogleMaps();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @OnClick(R.id.select_btn)
    public void onSelectClicked() {
        final SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();
        if (selectingHome) {
            SharedPreferencesUtils.putDouble(edit, "homeLongitude", googleMap.getCameraPosition().target.longitude);
            SharedPreferencesUtils.putDouble(edit, "homeLatitude", googleMap.getCameraPosition().target.latitude);
            selectingHome = false;
            selectButton.setText(R.string.select_work);
            edit.apply();
        } else {
            SharedPreferencesUtils.putDouble(edit, "workLongitude", googleMap.getCameraPosition().target.longitude);
            SharedPreferencesUtils.putDouble(edit, "workLatitude", googleMap.getCameraPosition().target.latitude);
            edit.apply();
            logHomeAndWork();
            Intent intent = new Intent(this, GoalActivity.class);
            startActivity(intent);
        }
    }

    private void logHomeAndWork() {
        final SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        Log.d("HOME", "" + SharedPreferencesUtils.getDouble(preferences, "homeLongitude", 0.0));
        Log.d("HOME", "" + SharedPreferencesUtils.getDouble(preferences, "homeLatitude", 0.0));
        Log.d("WORK", "" + SharedPreferencesUtils.getDouble(preferences, "workLongitude", 0.0));
        Log.d("WORK", "" + SharedPreferencesUtils.getDouble(preferences, "workLatitude", 0.0));
    }
}
