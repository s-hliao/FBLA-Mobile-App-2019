package com.hg.mad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hg.mad.R;

import java.util.List;

public class CompUsersSUtAdapter extends RecyclerView.Adapter<CompUsersSUtAdapter.ViewHolder> {

    private List<String> list;

    public CompUsersSUtAdapter(List list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView myCompTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myCompTitle = itemView.findViewById(R.id.my_comp_title);
        }

        public void bind(final String eventName) {
            myCompTitle.setText(eventName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String eventName = list.get(position);
        holder.bind(eventName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
