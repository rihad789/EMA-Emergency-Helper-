package com.service.ema;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.service.ema.Setting_Activity_Action.Setting_DatabaseHelper;

public class Splash_Screen_Activity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private TextView set_info_to_user;
    private static final String TAG = "Splash_Screen_Activity";

    private Location_DatabaseHelper L_DB;
    private Setting_DatabaseHelper setting_myDB;

    public static final String COL_4 = "ACTIVE_SIM";
    public static final String COL_5 = "ENABLED_SIM";

    double latitude; // latitude
    double longitude; // longitude
    // Declaring a Location Manager


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        //Initilizing Location Manager
        Log.d(TAG, "Initilizing Location Manager");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        L_DB = new Location_DatabaseHelper(this);
        set_info_to_user = findViewById(R.id.set_info_to_text_view);

        setting_myDB = new Setting_DatabaseHelper(this);

        if (check_setting_data() == 0) {
            Toast.makeText(this, "Set up Primary Data", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(Splash_Screen_Activity.this, Primary_setting_insert_data.class);
            startActivity(mainIntent);
            finish();
        }

        //Setting up primary Location Database
        Setup_Location_Database();
        //Showing info message if Location not retrieved in 5 s
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                set_info_to_user.setText("Please Move your device liitle Bit.\n if Application don't Start");
            }
        }, 8000);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Setting up Location Listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                //Updating Location database on Location Change
                Log.d(TAG, "Updating Location database on Location Change");
                UpdateLocationToDatabase(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //Sending user to Location setting when GPS not enabled
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        try {
            //Configuring and Setting up Location Manager
            configureLocatioService();
        } catch (Exception ignored) {
            Toast.makeText(this, "Configuration not possible", Toast.LENGTH_SHORT).show();
        }
    }

    //Updating New Location To Location Database
    public void UpdateLocationToDatabase(double Latitude, double Longitude) {

        boolean isUpdated = L_DB.updateData("1", String.valueOf(Latitude), String.valueOf(Longitude));
        if (isUpdated) {

            if (check_setting_data() != 0 && locationManager != null) {
                Intent mainIntent = new Intent(Splash_Screen_Activity.this, MainActivity.class);
                startActivity(mainIntent);
                locationManager = null;
                Log.d(TAG, "Removing Location Updates");
                finish();
                Toast.makeText(Splash_Screen_Activity.this, "Applocation Ready", Toast.LENGTH_SHORT).show();
            }

/*            if (locationManager != null) {
                locationManager = null;
                locationManager.removeUpdates(locationListener);
                Log.d(TAG, "Removing Location Updates");
            }*/
        }
    }

    public void send_To_MainActivity() {
        Intent mainIntent = new Intent(Splash_Screen_Activity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    //Setting up primary Location Database
    public void Setup_Location_Database() {
        Cursor res = L_DB.getAllData();
        if (res.getCount() == 0) {
            L_DB.insertData("0", "0");
        }
    }

    public int check_setting_data() {
        Cursor res = setting_myDB.getAllData();
        return res.getCount();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                configureLocatioService();
        }
    }

    private void configureLocatioService() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

        Log.d(TAG, "Initializing location manager");
    }

}


