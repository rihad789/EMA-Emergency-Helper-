package com.service.ema.Setting_Activity_Action;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.service.ema.BuildConfig;
import com.service.ema.PDF_Viewer;
import com.service.ema.R;

import java.util.List;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    private String SIM2_NAME = "";
    private String SIM1_NAME = "";
    private String SIM_ENABLED = "";
    private Toolbar toolbar;
    private AppCompatImageView name_edit_icon, message_edit_icon;
    private Setting_DatabaseHelper setting_myDB;
    private AppCompatTextView name_text_view, message_text_view, Submit_feedback_text_view, about_text_view;
    RelativeLayout Sim1_Layout, Sim2_Layout;

    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "MESSAGE";
    public static final String COL_4 = "ACTIVE_SIM";
    public static final String COL_5 = "ENABLED_SIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Initializing and setting up toolbar
        toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);

        //Setting up back to home button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initilizing feilds
        name_edit_icon = findViewById(R.id.name_edit_icon);
        message_edit_icon = findViewById(R.id.message_edit_icon);


        name_text_view = findViewById(R.id.setting_name_text_view);
        message_text_view = findViewById(R.id.setting_message_text_view);


        Submit_feedback_text_view = findViewById(R.id.feedback_label_text_view);
        about_text_view = findViewById(R.id.about_text_view);

        //Initilizing Setting Database Helper
        setting_myDB = new Setting_DatabaseHelper(this);

        get_Setting_Data();
    }


    @Override
    public void onStart() {
        super.onStart();

        name_edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Name edit clicked", Toast.LENGTH_SHORT).show();
                custom_setting_alert_name_with_editText();
            }
        });

        message_edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Message edit clicked", Toast.LENGTH_SHORT).show();
                custom_setting_alert_message_with_editText();
            }
        });


        //Click Listener for Submit feedback
        Submit_feedback_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_for_feedback_to_email();
                Toast.makeText(SettingActivity.this, "Submit feeedback Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //Click Listener for Privacy and Policy
        about_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Send_To_PDF_Activity = new Intent(SettingActivity.this, PDF_Viewer.class);
                Send_To_PDF_Activity.putExtra("Title", "About");
                Send_To_PDF_Activity.putExtra("PDF_Path", "firstpdf.pdf");
                startActivity(Send_To_PDF_Activity);
                Toast.makeText(SettingActivity.this, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Setting User Name
    public void custom_setting_alert_name_with_editText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edittext_for_setting_name, null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        AppCompatButton setting_dialog_cancel_name_button, setting_dialog_save_name_button;
        final TextInputEditText setting_dialog_editText_name;

        setting_dialog_cancel_name_button = dialogView.findViewById(R.id.setting_cancel_name_dialog_button);
        setting_dialog_save_name_button = dialogView.findViewById(R.id.setting_save_name_dialog_button);

        setting_dialog_editText_name = dialogView.findViewById(R.id.editText_setting_name);

        final AlertDialog dialog = builder.create();
        setting_dialog_save_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = setting_dialog_editText_name.getText().toString();
                boolean isUpdate_Name = setting_myDB.updateData("1", COL_2, name);
                if (isUpdate_Name) {
                    if (!name.isEmpty()) {
                        Toast.makeText(SettingActivity.this, "Name Saved", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        get_Setting_Data();
                    } else {
                        Toast.makeText(SettingActivity.this, "Please write your name", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "Name can nnot be Saved", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setting_dialog_cancel_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(SettingActivity.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
    }

    //Setting User Emergency Message
    public void custom_setting_alert_message_with_editText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edittext_for_setting_message, null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        AppCompatButton setting_dialog_cancel_message_button, setting_dialog_save_message_button;
        final TextInputEditText setting_dialog_editText_message;

        setting_dialog_cancel_message_button = dialogView.findViewById(R.id.setting_cancel_message_dialog_button);
        setting_dialog_save_message_button = dialogView.findViewById(R.id.setting_save_message_dialog_button);

        setting_dialog_editText_message = dialogView.findViewById(R.id.editText_setting_message);

        final AlertDialog dialog = builder.create();
        setting_dialog_save_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = setting_dialog_editText_message.getText().toString();
                boolean isUpdated_Message = setting_myDB.updateData("1", COL_3, message);
                if (isUpdated_Message) {

                    if (!message.isEmpty()) {
                        Toast.makeText(SettingActivity.this, "Message Saved", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        get_Setting_Data();
                    } else {
                        Toast.makeText(SettingActivity.this, "Please write some message", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SettingActivity.this, "Message can not be Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setting_dialog_cancel_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(SettingActivity.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


    //Get all Setting_Data
    public void get_Setting_Data() {
        Cursor res = setting_myDB.getAllData();
        String Name = "", Message = "";
        while (res.moveToNext()) {

            Name = res.getString(1);
            Message = res.getString(2);

        }
        //Setting setting values
        name_text_view.setText(Name);
        message_text_view.setText(Message);

    }

    public void send_user_for_feedback_to_email() {
        ShareCompat.IntentBuilder.from(SettingActivity.this)
                .setType("message/rfc822")
                .addEmailTo("support.ema24/7@gmail.com")
                .setText(device_info() + "\nYour Feedback here.....\n")
                .setChooserTitle("Choose an account")
                .startChooser();
    }

    public String device_info() {
        System.getProperty("os.version");
        String DEVICE = Build.BRAND;
        String MODEL = Build.MODEL;
        String version_SDK = String.valueOf(Build.VERSION.SDK_INT);
        String app_version = BuildConfig.VERSION_NAME;
        String app_name = BuildConfig.APPLICATION_ID;
        return "Application:" + app_name + " " + app_version + "\nDevice:" + DEVICE + " " + MODEL + "\nSDK Version:" + version_SDK;
    }

}

