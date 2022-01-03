package com.service.ema;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.service.ema.Setting_Activity_Action.Setting_DatabaseHelper;

import java.util.List;
import java.util.Objects;

public class Primary_setting_insert_data extends AppCompatActivity {

    private Setting_DatabaseHelper setting_myDB;
    private TextInputEditText setting_name, setting_message;
    private String ACTIVE_SIM = "0", ENALBED_SIM = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_setting_insert_data);

        setting_name = findViewById(R.id.setting_editText_primary_name);
        setting_message = findViewById(R.id.setting_edittext_primary_message);


        AppCompatButton setting_clear_feild_button = findViewById(R.id.setting_clear_dialog_button);
        AppCompatButton setting_save_feild_button = findViewById(R.id.setting_save_name_dialog_button);

        //Initilizing Setting Database Helper
        setting_myDB = new Setting_DatabaseHelper(this);

        setting_clear_feild_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_name.setText("");
                setting_message.setText("");
            }
        });


        setting_save_feild_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = Objects.requireNonNull(setting_name.getText()).toString();
                String message = Objects.requireNonNull(setting_message.getText()).toString();
                if (name.isEmpty()) {
                    Toast.makeText(Primary_setting_insert_data.this, "Pleaase Write your name", Toast.LENGTH_SHORT).show();
                } else if (message.isEmpty()) {
                    Toast.makeText(Primary_setting_insert_data.this, "Please write some message", Toast.LENGTH_SHORT).show();
                } else {

                    insert_primary_data_row(name, message);

                }
            }
        });

    }

    //Insert primary data if no data exist in first start
    public void insert_primary_data_row(String Name, String Message) {

        boolean insert_row = setting_myDB.insertData(Name, Message);
        if (insert_row) {

            Intent mainIntent = new Intent(Primary_setting_insert_data.this, Splash_Screen_Activity.class);
            startActivity(mainIntent);
            finish();
            Toast.makeText(this, "Application Ready.", Toast.LENGTH_SHORT).show();
        }
    }
}


