package com.hg.mad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hg.mad.R;

import java.util.List;
import java.util.Map;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {

    private List<Map<String, Object>> list;

    public AttendeeAdapter(List list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView inAttendance;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            inAttendance = itemView.findViewById(R.id.attendance);
            name  = itemView.findViewById(R.id.name);
        }

        public void bind(final String eventName, final boolean attendance) {
            name.setText(eventName);
            if(attendance) inAttendance.setText("In Attendance");
            else inAttendance.setText("");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> attendee = list.get(position);
        holder.bind((String)attendee.get("name"), (boolean)attendee.get("signedIn"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
