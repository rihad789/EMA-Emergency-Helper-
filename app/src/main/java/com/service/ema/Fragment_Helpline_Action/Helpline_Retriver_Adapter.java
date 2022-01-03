package com.service.ema.Fragment_Helpline_Action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.service.ema.Fragment_Contacts_Action.contact_POJO_class;
import com.service.ema.R;

import java.util.List;

public class Helpline_Retriver_Adapter extends RecyclerView.Adapter<Helpline_Retriver_Adapter.postviewholder> {

    private Context context;
    private List<Helpline_POJO_Class> Helpline_contactList;
    public AppCompatImageView call_action;

    //Contact Retriver Adapter Constructor
    public Helpline_Retriver_Adapter(Context context, List<Helpline_POJO_Class> Helpline_contactList) {
        this.context = context;
        this.Helpline_contactList = Helpline_contactList;
    }

    @NonNull
    @Override
    public Helpline_Retriver_Adapter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.helpline_contact_display_layout, parent, false);
        Helpline_Retriver_Adapter.postviewholder holder = new Helpline_Retriver_Adapter.postviewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Helpline_Retriver_Adapter.postviewholder holder, final int position) {

        final Helpline_POJO_Class contacts = Helpline_contactList.get(position);
            holder.contacts_name.setText(contacts.getHelpline_contacts_name());
            holder.service_area.setText(contacts.getHelpline_service_area());
            holder.contacts_phone.setText(contacts.getHelpline_contacts_no());


        call_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = holder.getAdapterPosition();
                Helpline_POJO_Class get_Number = Helpline_contactList.get(position);
                String number = get_Number.getHelpline_contacts_no();

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(Uri.parse("tel:" + number));
                context.startActivity(callIntent);

                Toast.makeText(context, "Contact ready to Call ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (Helpline_contactList == null) {
            return 0;
        } else {
            return Helpline_contactList.size();
        }

    }

    public class postviewholder extends RecyclerView.ViewHolder {

        AppCompatTextView contacts_name, contacts_phone, service_area;

        public postviewholder(@NonNull View itemView) {
            super(itemView);
            contacts_name = itemView.findViewById(R.id.helpline_contacts_name);
            contacts_phone = itemView.findViewById(R.id.helpline_contacts_no);
            service_area = itemView.findViewById(R.id.helpline_service_area);
            call_action = itemView.findViewById(R.id.helpline_option_call);

        }
    }
}
