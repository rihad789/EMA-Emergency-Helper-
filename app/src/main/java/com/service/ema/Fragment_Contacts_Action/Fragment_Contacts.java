package com.service.ema.Fragment_Contacts_Action;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.service.ema.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Contacts extends Fragment {

    private View view;
    private FloatingActionButton add_contact_floating_action;
    private RecyclerView recyclerView;
    private List<contact_POJO_class> contactlist;
    private contact_Retriver_adapter adapter;
    private RelativeLayout empty_contactList_view;
    private contact_DatabaseHelper mydb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment__contacts, container, false);
        add_contact_floating_action = view.findViewById(R.id.floating_add_contacts);

        mydb = new contact_DatabaseHelper(getContext());

        // Inflate the layout for this fragment
        empty_contactList_view = view.findViewById(R.id.empty_contactList_view);
        recyclerView = view.findViewById(R.id.contacts_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactlist = new ArrayList<>();

        getAllData();

        adapter = new contact_Retriver_adapter(getContext(), contactlist);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (contactlist.size() > 0) {
            empty_contactList_view.setVisibility(View.INVISIBLE);
        }
        add_contact_floating_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_add_contact_dialog();

            }
        });
    }

    public void custom_add_contact_dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_contacts_dialog_layout, null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        AppCompatButton dialog_cancel_button, dialog_save_button;
        final TextInputEditText dialog_editText_name, dialog_EditText_phone;

        dialog_cancel_button = dialogView.findViewById(R.id.contact_cancel_dialog_button);
        dialog_save_button = dialogView.findViewById(R.id.contact_save_name_dialog_button);

        dialog_editText_name = dialogView.findViewById(R.id.editText_contact_name);
        dialog_EditText_phone = dialogView.findViewById(R.id.edittext_contact_phone);

        final AlertDialog dialog = builder.create();
        dialog_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String name, phone;
                    name = dialog_editText_name.getText().toString();
                    phone = dialog_EditText_phone.getText().toString();
                    //Saving contacts to Database
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Please write your name ", Toast.LENGTH_SHORT).show();
                    } else if (phone.isEmpty()) {
                        Toast.makeText(getContext(), "Please write phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        if (getContactCount() >= 4) {
                            Toast.makeText(getContext(), "Can't add more than 3 Contacts", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isInserted = mydb.insertData(name, phone);
                            if (isInserted) {
                                Toast.makeText(getContext(), "Contacts Saved.", Toast.LENGTH_SHORT).show();
                                reload_fragment();
                                dialog.cancel();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error:" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Operation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void getAllData() {
        Cursor res = mydb.getAllData();
        contactlist.clear();
        while (res.moveToNext()) {
            contact_POJO_class contact = new contact_POJO_class();
            contact.setID(res.getString(0));
            contact.setName(res.getString(1));
            contact.setPhone(res.getString(2));
            contactlist.add(contact);
        }

    }

    private int getContactCount() {
        Cursor res = mydb.getAllData();
        return res.getCount();
    }

    public void reload_fragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new Fragment_Contacts());
        fragmentTransaction.commit();
    }

}