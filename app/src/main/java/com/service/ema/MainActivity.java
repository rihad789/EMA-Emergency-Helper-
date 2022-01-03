package com.service.ema;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.service.ema.Fragment_Contacts_Action.Fragment_Contacts;
import com.service.ema.Fragment_Helpline_Action.Fragment_Helplines;
import com.service.ema.Setting_Activity_Action.SettingActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Fragment_Home fragment_Home;
    private Fragment_Contacts fragment_contacts;
    private Fragment_Helplines fragment_helplines;
    private Location_DatabaseHelper L_DB;

    private double Latitude, Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initilizing Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        L_DB = new Location_DatabaseHelper(this);

        FrameLayout fragment_container = findViewById(R.id.fragment_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        fragment_Home = new Fragment_Home();
        fragment_contacts = new Fragment_Contacts();
        fragment_helplines = new Fragment_Helplines();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_Home).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_button:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_Home).commit();
                        return true;

                    case R.id.contacts_button:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_contacts).commit();
                        return true;

                    case R.id.helpline_button:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_helplines).commit();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_button:
                shareapp();
                break;
            case R.id.setting_button:
                Send_To_Setting();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Setting up Location Listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                //Updating Location database on Location Change
                UpdateLocationToDatabase(Latitude, Longitude);
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
    }

    //Updating New Location To Location Database
    public void UpdateLocationToDatabase(double Latitude, double Longitude) {
        boolean isUPDATED=L_DB.updateData("1", String.valueOf(Latitude), String.valueOf(Longitude));
        Toast.makeText(MainActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
    }

    //Setting up Share app to other user
    public void shareapp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This app may save your life in time.Check this app at :https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    //Sending user to App Setting Activity
    public void Send_To_Setting() {
        Intent Send_To_Setting = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(Send_To_Setting);
    }

}

