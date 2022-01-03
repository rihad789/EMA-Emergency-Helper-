package com.service.ema;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.textfield.TextInputEditText;
import com.service.ema.Fragment_Contacts_Action.contact_DatabaseHelper;
import com.service.ema.Fragment_Contacts_Action.contact_POJO_class;
import com.service.ema.Setting_Activity_Action.Setting_DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Fragment_Home extends Fragment {

    private static final int SEND_SMS_PERMISSION_CODE = 100;
    private AppCompatButton alert_message_sender_button;
    private String Latitude, Longitude;
    private String Name = "", Message = "";

    private Location_DatabaseHelper L_DB;
    private contact_DatabaseHelper Con_DB;
    private Setting_DatabaseHelper Sett_DB;
    private String SENT = "SOS_MESSAGE SENT";

    private String DELIVERED = "SOS_MESSAGE_DELIVERED";
    PendingIntent SentPI, DeliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliverReceiver;
    private ArrayList<String> phoneList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__home, container, false);
        alert_message_sender_button = view.findViewById(R.id.alert_button);

        //Setting up required Databases Setting?_Dattabase,Location_Datbase,Contact_Database
        L_DB = new Location_DatabaseHelper(getContext());
        Con_DB = new contact_DatabaseHelper(getContext());
        Sett_DB = new Setting_DatabaseHelper(getContext());

        //Initilizing Pending Intent for SMS Manager
        SentPI = PendingIntent.getBroadcast(getContext(), 0, new Intent(SENT), 0);
        DeliveredPI = PendingIntent.getBroadcast(getContext(), 0, new Intent(DELIVERED), 0);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Send SOS button click event
        alert_message_sender_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (get_contact_count() == 0) {
                    Toast.makeText(getContext(), "No Contacts Found.", Toast.LENGTH_SHORT).show();
                } else {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setMessage("Would you like to send the SOS?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Send_SMS();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //SMS Sending confirmation / Error Result generator
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), "SMS_Sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic Error", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Error no Service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio OFF", Toast.LENGTH_SHORT).show();
                }

            }
        };

        smsDeliverReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "Sms Delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "Sms not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        getActivity().registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        getActivity().registerReceiver(smsDeliverReceiver, new IntentFilter(DELIVERED));

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(smsSentReceiver);
        getActivity().unregisterReceiver(smsDeliverReceiver);
    }

    public String getData() {
        Cursor res_Location = L_DB.getAllData();
        while (res_Location.moveToNext()) {
            Latitude = res_Location.getString(1);
            Longitude = res_Location.getString(2);
        }

        Cursor res_PhoneList = Con_DB.getAllData();
        phoneList.clear();
        while (res_PhoneList.moveToNext()) {
            phoneList.add(res_PhoneList.getString(2));
        }

        Cursor res_Setting = Sett_DB.getAllData();
        while (res_Setting.moveToNext()) {
            Name = res_Setting.getString(1);
            Message = res_Setting.getString(2);
        }

        String sendToGMap="https://maps.google.com/?q="+Latitude+","+Longitude;
        return "I am " + Name + "." + Message+"." + "See my Last Location on Map: "+sendToGMap;
    }

    //Getting contact count from Contact Database
    public int get_contact_count() {
        Cursor res = Con_DB.getAllData();
        return res.getCount();
    }


    public void Send_SMS() {
        //Creating an Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        String info = getData();
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
        //Checking Message Permissions
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.SEND_SMS}
                    , SEND_SMS_PERMISSION_CODE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 600);

        } else {
            //Sending SOS Mesage
            int delay = 0;
            for (int i = 0; i < phoneList.size(); i++) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneList.get(i), null, info, SentPI, DeliveredPI);
                delay = (i + 1) * 1000;
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Processing Please Wait......", Toast.LENGTH_SHORT).show();
                }
            }, delay);
        }
        dialog.show();
    }
}


