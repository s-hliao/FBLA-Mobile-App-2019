package com.hg.mad.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hg.mad.R;
import com.hg.mad.model.CompetitiveEvent;
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

        public void bind(final DocumentSnapshot snapshot,
                         final OnOfficerListener listener) {

            Officer officer = snapshot.toObject(Officer.class);


            nameTextView.setText(officer.getName());
            positionTextView.setText(officer.getPosition());
            contactTextView.setText(officer.getContact());
            String profile = officer.getProfile();

            if(profile!=null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child(profile);

                imageRef.getBytes(20000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        profilePic.setImageBitmap(
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length)
                        );
                    }
                });


            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOfficerSelected(snapshot);
                    }
                }
            });
        }
    }

    public interface OnOfficerListener {

        void onOfficerSelected(DocumentSnapshot chapOfficer);

    }

    private OnOfficerListener listener;

    public OfficerAdapter(Query query, OnOfficerListener listener) {
        super(query);
        this.listener = listener;
    }

    public OfficerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View officerView = inflater.inflate(R.layout.item_officer, parent, false);

        ViewHolder viewHolder = new ViewHolder(officerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfficerAdapter.ViewHolder viewHolder, int row){
        viewHolder.bind(getSnapshot(row), listener);

    }


}
