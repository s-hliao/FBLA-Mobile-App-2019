package com.hg.mad.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.ManageCompActivity;
import com.hg.mad.R;
import com.hg.mad.adapter.ChapterEventAdapter;
import com.hg.mad.adapter.CompetitiveAdapter;
import com.hg.mad.dialog.ChapEventDialogFragment;
import com.hg.mad.dialog.CompSUDialogFragment;
import com.hg.mad.dialog.CompetitiveDialogFragment;
import com.hg.mad.dialog.FilterDialogFragment;
import com.hg.mad.dialog.Filters;
import com.hg.mad.dialog.TakeAttendanceDialogFragment;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.util.ThisUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ChapterCalendarFragment extends Fragment implements ChapterEventAdapter.OnChapListener {

    private CalendarView eventCalendar;
    private RecyclerView eventRecycler;
    private CollectionReference chapterEvents;
    private List<EventDay> events;
    private ChapterEventAdapter adapter;
    private Query query;

    private TakeAttendanceDialogFragment attendanceDialog;
    private ChapEventDialogFragment chapEventDialog;

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapter_calendar, container, false);

        eventCalendar = root.findViewById(R.id.calendarView);
        eventRecycler = root.findViewById(R.id.event_recycler);
        events = new ArrayList<>();

        attendanceDialog = new TakeAttendanceDialogFragment();
        chapEventDialog = new ChapEventDialogFragment();

        eventCalendar.setHeaderColor(Color.parseColor("#3666a8"));

        final ChapterEventAdapter.OnChapListener o = this;

        FirebaseFirestore.getInstance().collection("Chapter")
                .whereEqualTo("chapterName", ThisUser.getChapterName()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        chapterEvents = queryDocumentSnapshots.getDocuments().get(0).getReference()
                                            .collection("ChapterEvent");

                        chapterEvents.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot ds:queryDocumentSnapshots){
                                    System.out.println("done");
                                    Date d  = ds.getDate("date");
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(d);
                                    events.add(new EventDay(c, R.drawable.dot, Color.parseColor("#314fbd")));
                                }
                                eventCalendar.setEvents(events);
                            }
                        });



                        chapterEvents.addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                System.out.println("listen:error"+e);
                                return;
                            }
                            events = new ArrayList<>();
                            for(DocumentSnapshot ds:snapshots){
                                System.out.println("done");
                                Date d  = ds.getDate("date");
                                Calendar c1 = Calendar.getInstance();
                                c1.setTime(d);
                                events.add(new EventDay(c1, R.drawable.dot, Color.parseColor("#314fbd")));
                            }
                            eventCalendar.setEvents(events);

                            System.out.println("events changed");


                        });

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String s = dateFormat.format(Calendar.getInstance().getTime());

                        try {
                            query = chapterEvents.whereEqualTo("date", dateFormat.parse(s)).orderBy("lower");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        adapter = new ChapterEventAdapter(query, o);
                        adapter.setQuery(query);

                        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        eventRecycler.setAdapter(adapter);
                        eventRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                        eventCalendar.setOnDayClickListener(new OnDayClickListener() {

                            @Override
                            public void onDayClick(EventDay eventDay) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                String s = dateFormat.format(eventDay.getCalendar().getTime());

                                System.out.println(s);

                                try {
                                    query = chapterEvents.whereEqualTo("date", dateFormat.parse(s)).orderBy("eventName");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                adapter.setQuery(query);

                            }
                        });

                    }
                });

        return root;
    }



    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }

    @Override
    public void onChapSelected(DocumentSnapshot chapEvent) {
        FirebaseFirestore.getInstance().collection("Chapter")
                .whereEqualTo("chapterName", ThisUser.getChapterName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot chapRef = queryDocumentSnapshots.getDocuments().get(0);
                Map<String,  Map<String, Map<String, Object>>> events = (Map) chapRef.get("chapterEvents");
                String eventName = (String) chapEvent.get("eventName");

                if (events.containsKey(eventName) && events.get(eventName).containsKey(ThisUser.getUid())){
                    boolean attendance = chapEvent.getBoolean("attendanceActive");
                    attendanceDialog.setAttendanceActive(attendance);
                    attendanceDialog.setEventName(chapEvent.get("eventName").toString());

                    if(attendance)attendanceDialog.setAttendancePassword(chapEvent.get("signInKey").toString());

                    showAttendance(chapEvent);

                } else {
                    chapEventDialog.setEventName(eventName);

                    getFragmentManager().executePendingTransactions();
                    if (!chapEventDialog.isAdded())
                        chapEventDialog.show(getFragmentManager(), "ChapEventsDialog");
                }
            }
        });
    }

    private void showAttendance(final DocumentSnapshot s){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        // Update Chapter
        chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                    Map<String, Map<String, Map<String, Object>>> currentEventsChap = (Map) chapter.get("chapterEvents");
                    Map<String, Object> user = currentEventsChap.get(s.get("eventName")).get(ThisUser.getUid());
                    if ((boolean) user.get("signedIn")) {
                        attendanceDialog.setAttendanceActive(false);
                    }

                    getFragmentManager().executePendingTransactions();
                    if(!attendanceDialog.isAdded())
                        attendanceDialog.show(getFragmentManager(), "takeAttendanceDialog");
                }
            }
        });
    }
}
