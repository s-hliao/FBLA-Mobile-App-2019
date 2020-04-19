package com.hg.mad.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.hg.mad.R;
import com.hg.mad.model.ChapterEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChapterEventAdapter extends FirestoreAdapter<ChapterEventAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public TextView dateText;
        public TextView typeText;
        public TextView attendance;
        public TextView description;

        public ViewHolder (View itemView){
            super(itemView);

            nameText = itemView.findViewById(R.id.chapter_event);
            description = itemView.findViewById(R.id.description);
            dateText = itemView.findViewById(R.id.date);
            typeText = itemView.findViewById(R.id.event_type);
            attendance = itemView.findViewById(R.id.attendance_managed);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnChapListener listener) {

            ChapterEvent event = snapshot.toObject(ChapterEvent.class);

            nameText.setText(event.getEventName());
            description.setText(event.getDescription());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = event.getDate();
            dateText.setText(dateFormat.format(date));

            Date now = new Date();
            if (dateFormat.format(now).equals(dateFormat.format(date)))
                dateText.setTextColor(Color.parseColor("#32cd32"));
            else if (date.before(now))
                dateText.setTextColor(Color.parseColor("#d16b73"));
            else
                dateText.setTextColor(Color.parseColor("#a9a9a9"));

            typeText.setText(event.getEventType());
            if (event.getAttendanceActive())
                attendance.setText("Attendance Taken");
            else
                attendance.setText("No Attendance");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onChapSelected(snapshot);
                    }
                }
            });
        }
    }

    public interface OnChapListener {

        void onChapSelected(DocumentSnapshot chapEvent);

    }

    private OnChapListener listener;

    public ChapterEventAdapter(Query query, OnChapListener listener) {
        super(query);
        this.listener = listener;
    }

    public ChapterEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_chapter_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChapterEventAdapter.ViewHolder viewHolder, int row){
        viewHolder.bind(getSnapshot(row), listener);

    }
}
