package com.service.ema.Fragment_Helpline_Action;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.service.ema.Fragment_Contacts_Action.contact_DatabaseHelper;
import com.service.ema.Fragment_Contacts_Action.contact_POJO_class;
import com.service.ema.Fragment_Contacts_Action.contact_Retriver_adapter;
import com.service.ema.R;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Helplines extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<Helpline_POJO_Class> Helpline_contactlist;
    private Helpline_Retriver_Adapter adapter;
    private DatabaseAccess helpLine_DB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__helplines, container, false);
        helpLine_DB = DatabaseAccess.getInstance(getContext());

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.helpline_contacts_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Helpline_contactlist = new ArrayList<>();


        getAllData();

        adapter = new Helpline_Retriver_Adapter(getContext(), Helpline_contactlist);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getAllData() {

        helpLine_DB.open();
        Cursor res = helpLine_DB.getAllData();

        Helpline_contactlist.clear();
        while (res.moveToNext()) {
            Helpline_POJO_Class contact = new Helpline_POJO_Class();
            contact.setHelpline_contacts_name(res.getString(0));
            contact.setHelpline_contacts_no(res.getString(1));
            contact.setHelpline_service_area("Service Area:" + res.getString(2));

            Helpline_contactlist.add(contact);
        }
        helpLine_DB.close();
    }
}