package com.hg.mad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hg.mad.R;

import java.util.List;

public class MyCompEventAdapter extends RecyclerView.Adapter<MyCompEventAdapter.ViewHolder> {

    private List<String> list;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView myCompTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myCompTitle = itemView.findViewById(R.id.my_comp_title);
        }

        public void bind(String s) {
            myCompTitle.setText(s);
        }


    }

    public MyCompEventAdapter(List<String> list) {
        this.list = list;
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
        String currentString = list.get(position);
        holder.bind(currentString);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
