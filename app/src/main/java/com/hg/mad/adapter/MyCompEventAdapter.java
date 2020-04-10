package com.hg.mad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.hg.mad.R;
import com.hg.mad.dialog.CompSUDialogFragment;

import java.util.List;

public class MyCompEventAdapter extends RecyclerView.Adapter<MyCompEventAdapter.ViewHolder> {

    private List<String> list;

    public interface OnMyCompListener {

        void onMyCompSelected(String eventName);

    }

    private MyCompEventAdapter.OnMyCompListener listener;

    public MyCompEventAdapter(List list, MyCompEventAdapter.OnMyCompListener listener) {
        this.list = list;
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView myCompTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myCompTitle = itemView.findViewById(R.id.my_comp_title);
        }

        public void bind(final String eventName, final OnMyCompListener listener) {
            myCompTitle.setText(eventName);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onMyCompSelected(eventName);
                    }
                }
            });
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_comp, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String eventName = list.get(position);
        holder.bind(eventName, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
