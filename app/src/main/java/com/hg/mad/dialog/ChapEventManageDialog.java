 package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.adapter.AttendeeAdapter;
import com.hg.mad.adapter.CompSUAdapter;
import com.hg.mad.model.Attendee;
import com.hg.mad.model.ChapterEvent;
import com.hg.mad.util.ThisUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

 public class ChapEventManageDialog extends DialogFragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView attendeeRecycler;
    private TextView nameView;
    private String eventName;
    private Button cancel;
    private Button reset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_chap_event_manage, container, false);
        cancel =rootView.findViewById(R.id.button_cancel);
        reset = rootView.findViewById(R.id.button_reset);
        attendeeRecycler = rootView.findViewById(R.id.recycler_attendees);

        nameView = rootView.findViewById(R.id.comp_name);
        nameView.setText("Sign ups for "+ eventName);

        rootView.findViewById(R.id.recycler_event);

        cancel.setOnClickListener(this);
        reset.setOnClickListener(this);

        getAttendees();

        return rootView;
    }

    public void setEventName(String name){
        this.eventName = name;
    }


     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                onCancelClicked();
                break;
            case R.id.button_reset:
                onResetClicked();
                break;
        }
    }

    public void onResetClicked(){
        FirebaseFirestore.getInstance().collection("Chapter")
                .whereEqualTo("chapterName", ThisUser.getChapterName()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot chapter = queryDocumentSnapshots.getDocuments().get(0);

                        Map<String, Map<String, Map<String, Object>>>attendees = (Map)chapter.get("chapterEvents");
                        attendees.put(eventName, new HashMap<String, Map<String, Object>>());
                        chapter.getReference().update("chapterEvents", attendees);
                    }
                });
        Toast.makeText(getContext(), "Attendees Removed", Toast.LENGTH_SHORT).show();
        dismiss();
    }

     public void getAttendees() {

         FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
         final CollectionReference chaptersCollection = fireStore.collection("Chapter");

           chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                 .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()) {

                     DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                     Map<String, Map<String, Map<String, Object>>> currentEventsChap = (Map) chapter.get("chapterEvents");

                     if (currentEventsChap != null && currentEventsChap.containsKey(eventName)) {
                         Collection<Map<String, Object>> users = currentEventsChap.get(eventName).values();
                         List usersArray = users.stream().collect(Collectors.toList());

                         AttendeeAdapter adapter = new AttendeeAdapter(usersArray);
                         attendeeRecycler.setAdapter(adapter);
                         attendeeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                     } else {
                         AttendeeAdapter adapter = new AttendeeAdapter(new ArrayList<Map<String, Object>>());
                         attendeeRecycler.setAdapter(adapter);
                         attendeeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                     }

                 }
             }
         });
     }

    public void onCancelClicked() {
        dismiss();
    }

     @Override
     public void onResume() {
         super.onResume();

         getDialog().getWindow().setLayout(
                 ViewGroup.LayoutParams.MATCH_PARENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
     }
}
