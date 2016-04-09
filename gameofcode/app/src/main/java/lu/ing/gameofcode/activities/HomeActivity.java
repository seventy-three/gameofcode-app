package lu.ing.gameofcode.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import lu.ing.gameofcode.utils.SharedPreferencesUtils;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        final SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        if (SharedPreferencesUtils.getDouble(preferences, "workLongitude", 0) != 0) {
            startActivity(new Intent(this, PlanningActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();

    }


}
