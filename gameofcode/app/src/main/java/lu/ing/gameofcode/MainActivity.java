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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import lu.ing.gameofcode.line.BusLine;
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

        /////////////////////////////////////////////////////
//        BusLine line = new BusLine(this);
//        try {
//            line.getAvailableLines("49599457","6132893");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        /////////////////////////////////////////////////////
        final OkHttpClient client = new OkHttpClient();

        // Create request for remote resource.
        final Request request = new Request.Builder()
                .url("http://google.com")
                .build();

        // Execute the request and retrieve the response.


        spiceManager.execute(new SpiceRequest<String>(String.class) {
            @Override
            public String loadDataFromNetwork() throws Exception {
                try {
                    return client.newCall(request).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new RequestListener<String>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(String s) {
                Log.d("MAIN", s);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
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
