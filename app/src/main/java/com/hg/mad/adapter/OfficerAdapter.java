package com.hg.mad.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.model.Officer;

import java.util.ArrayList;
import java.util.List;

public class OfficerAdapter extends FirestoreAdapter<OfficerAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTextView;
        public TextView positionTextView;
        public TextView contactTextView;
        public ImageView profilePic;


        public ViewHolder (View itemView){
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.textView1);
            positionTextView = (TextView) itemView.findViewById(R.id.textView2);
            contactTextView = (TextView) itemView.findViewById(R.id.textView10);
            profilePic = (ImageView) itemView.findViewById(R.id.imageView3);
        }
    }

    public interface OnOfficerListener {

        void onOfficerSelected(DocumentSnapshot chapOfficer);

    }



    public OfficerAdapter(Query query) {
        super(query);
    }

    public OfficerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View officerView = inflater.inflate(R.layout.item_officer, parent, false);

        ViewHolder viewHolder = new ViewHolder(officerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfficerAdapter.ViewHolder viewHolder, int row){
        Officer officer = getSnapshot(row).toObject(Officer.class);


        viewHolder.nameTextView.setText(officer.getName());
        viewHolder.positionTextView.setText(officer.getPosition());
        viewHolder.contactTextView.setText(officer.getContact());
        byte[]data = officer.getProfileImage();
        if(data.length!=0) {
            viewHolder.profilePic.setImageBitmap(
                    BitmapFactory.decodeByteArray(data, 0, data.length)
            );
        }
    }


}
