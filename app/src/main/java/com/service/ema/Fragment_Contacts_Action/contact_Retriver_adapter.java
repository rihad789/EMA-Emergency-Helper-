package com.service.ema.Fragment_Contacts_Action;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.service.ema.R;
import java.util.List;

public class contact_Retriver_adapter extends RecyclerView.Adapter<contact_Retriver_adapter.postviewholder> {

    private Context context;
    private List<contact_POJO_class> contactList;
    public AppCompatImageView call_action;
    RelativeLayout contacts_ino_layout;
    contact_DatabaseHelper myDB;

    //Contact Retriver Adapter Constructor
    public contact_Retriver_adapter(Context context, List<contact_POJO_class> contactList) {
        this.context = context;
        this.contactList = contactList;
    }


    @NonNull
    @Override
    public contact_Retriver_adapter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contacts_display_layout, parent, false);
        postviewholder holder = new postviewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final contact_Retriver_adapter.postviewholder holder, final int position) {

        final contact_POJO_class contacts = contactList.get(position);
        holder.contacts_name.setText(contacts.getName());
        holder.contacts_phone.setText(contacts.getPhone());

        call_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Contact ready to Call ", Toast.LENGTH_SHORT).show();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(Uri.parse("tel:" + contacts.getPhone()));
                context.startActivity(callIntent);

            }
        });

        contacts_ino_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete the contact?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.deleteData(contacts.getID());
                                contactList.remove(position);
                                notifyDataSetChanged();
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        if (contactList == null) {
            return 0;
        } else {
            return contactList.size();
        }

    }

    public class postviewholder extends RecyclerView.ViewHolder {

        AppCompatTextView contacts_name, contacts_phone;

        public postviewholder(@NonNull View itemView) {
            super(itemView);
            contacts_name = itemView.findViewById(R.id.contacts_name);
            contacts_phone = itemView.findViewById(R.id.contacts_no);
            call_action = itemView.findViewById(R.id.option_call);
            contacts_ino_layout = itemView.findViewById(R.id.contacts_ino_layout);

            myDB = new contact_DatabaseHelper(context);

        }
    }

}
