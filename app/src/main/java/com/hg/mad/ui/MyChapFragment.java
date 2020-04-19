package com.hg.mad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.adapter.MyChapEventAdapter;
import com.hg.mad.dialog.ChapEventDialogFragment;
import com.hg.mad.dialog.CompSUDialogFragment;
import com.hg.mad.dialog.TakeAttendanceDialogFragment;
import com.hg.mad.util.ThisUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyChapFragment extends Fragment
        implements MyChapEventAdapter.OnMyChapListner {

    private View root;

    private RecyclerView myChapRecycler;
    private LinearLayoutManager layoutManager;
    private MyChapEventAdapter adapter;
    private List<String> myChap;

    TakeAttendanceDialogFragment takeAttendanceDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_mychap, container, false);

        // Recycler Views
        myChap = new ArrayList<>();
        myChapRecycler = root.findViewById(R.id.recycler_my_chap);
        layoutManager = new LinearLayoutManager(getContext());
        myChapRecycler.setLayoutManager(layoutManager);
        myChapRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        takeAttendanceDialog = new TakeAttendanceDialogFragment();

        updateChapAdapter();
        beganUpdating();

        return root;
    }

    private void beganUpdating() {
        DocumentReference databaseUserRef = FirebaseFirestore.getInstance().collection("DatabaseUser")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // TODO
        // Set up competitive events listener
        databaseUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists() && snapshot.get("chapterEvents") != null) {
                    myChap = new ArrayList<>();
                    Map<String, Integer> events = (Map<String, Integer>) snapshot.get("chapterEvents");
                    myChap.addAll(events.keySet());

                    updateChapAdapter();
                }
            }
        });
    }

    private void updateChapAdapter(){
        adapter = new MyChapEventAdapter(myChap, this);
        myChapRecycler.setAdapter(adapter);
    }

    @Override
    public void onMyChapSelected(final String eventName) {

        FirebaseFirestore.getInstance().collection("Chapter")
                .whereEqualTo("chapterName", ThisUser.getChapterName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot chapDoc = queryDocumentSnapshots.getDocuments().get(0);
                Map<String, Map<String, Map<String, Object>>> events = (Map) chapDoc.get("chapterEvents");

                // Show the attendance
                if (events.containsKey(eventName) && events.get(eventName).containsKey(ThisUser.getUid())) {

                    chapDoc.getReference().collection("ChapterEvent").whereEqualTo("eventName", eventName)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            DocumentSnapshot chapterEventSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            boolean attendance = chapterEventSnapshot.getBoolean("attendanceActive");

                            takeAttendanceDialog.setAttendanceActive(attendance);
                            takeAttendanceDialog.setEventName(chapterEventSnapshot.get("eventName").toString());
                            if (attendance)
                                takeAttendanceDialog.setAttendancePassword(chapterEventSnapshot.get("signInKey").toString());

                            showAttendance(chapterEventSnapshot);
                        }
                    });
                }

                // Show the sign up dialog
                else {
                    ChapEventDialogFragment chapterEventDialog = new ChapEventDialogFragment();
                    chapterEventDialog.setEventName(eventName);
                    getFragmentManager().executePendingTransactions();
                    if (!chapterEventDialog.isAdded())
                        chapterEventDialog.show(getFragmentManager(), "chapterEventDialog");
                }
            }
        });
    }

    private void showAttendance(final DocumentSnapshot chapterEventSnapshot){

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        // Decide whether the user has already signed in
        chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                    Map<String, Map<String, Map<String, Object>>> currentEventsChap = (Map) chapter.get("chapterEvents");
                    Map<String, Object> user = currentEventsChap.get(chapterEventSnapshot.get("eventName")).get(ThisUser.getUid());
                    if ((boolean) user.get("signedIn")) {
                        takeAttendanceDialog.setAttendanceActive(false);
                    }

                    getFragmentManager().executePendingTransactions();
                    if(!takeAttendanceDialog.isAdded())
                        takeAttendanceDialog.show(getFragmentManager(), "takeAttendanceDialog");
                }
            }
        });
    }
}
