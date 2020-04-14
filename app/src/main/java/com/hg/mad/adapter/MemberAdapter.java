package com.hg.mad.adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hg.mad.R;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.model.Officer;

public class MemberAdapter extends FirestoreAdapter<MemberAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTextView;

        public TextView adminText;


        public ViewHolder (View itemView){
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.text_name);
            adminText = itemView.findViewById(R.id.admin_text);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnMemberListener listener) {

            DatabaseUser user = snapshot.toObject(DatabaseUser.class);

            nameTextView.setText(user.getName());
            if(user.getIsAdmin()){
                adminText.setVisibility(View.VISIBLE);
            } else{
                adminText.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onMemberSelected(snapshot);
                    }
                }
            });
        }
    }

    public interface OnMemberListener {

        void onMemberSelected(DocumentSnapshot chapOfficer);

    }

    private OnMemberListener listener;

    public MemberAdapter(Query query, OnMemberListener listener) {
        super(query);
        this.listener = listener;
    }

    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View memberView = inflater.inflate(R.layout.item_member, parent, false);

        ViewHolder viewHolder = new ViewHolder(memberView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder viewHolder, int row){
        viewHolder.bind(getSnapshot(row), listener);

    }


}
