package com.hg.mad.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.SignedInActivity;
import com.hg.mad.adapter.ChapterEventAdapter;
import com.hg.mad.adapter.CompetitiveAdapter;
import com.hg.mad.adapter.OfficerAdapter;
import com.hg.mad.dialog.AddChapEventDialogFragment;
import com.hg.mad.dialog.AddOfficerDialogFragment;
import com.hg.mad.dialog.ChapEventDialogFragment;
import com.hg.mad.dialog.EditChapEventDialogFragment;
import com.hg.mad.dialog.EditOfficerDialogFragment;
import com.hg.mad.dialog.MenuDialogFragment;
import com.hg.mad.dialog.SocMediaDialogFragment;
import com.hg.mad.dialog.TakeAttendanceDialogFragment;
import com.hg.mad.model.Chapter;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.model.Officer;
import com.hg.mad.util.ThisUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Signed;

public class ChapterEventsFragment extends Fragment implements
        View.OnClickListener, ChapterEventAdapter.OnChapListener{

    private LinearLayout addEventButton;
    private View divider;

    private RecyclerView eventRV;

    private DocumentReference chapterRef;
    private FirebaseFirestore firestore;
    private Query query;

    private ChapterEventAdapter adapter;

    private MenuDialogFragment menuDialog;
    private AddChapEventDialogFragment addEventDialog;
    private TakeAttendanceDialogFragment attendanceDialog;
    private ChapEventDialogFragment chapEventDialog;

    private boolean isAdmin;
    private String chapterName;

    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapter_events, container, false);

        eventRV = root.findViewById(R.id.recycler_chap_events);
        addEventButton = root.findViewById(R.id.button_add_chap);
        divider = root.findViewById(R.id.divider5);

        addEventButton.setOnClickListener(this);

        menuDialog = new MenuDialogFragment();
        addEventDialog = new AddChapEventDialogFragment();
        attendanceDialog = new TakeAttendanceDialogFragment();
        chapEventDialog = new ChapEventDialogFragment();

        FirebaseFirestore.setLoggingEnabled(true);
        firestore = FirebaseFirestore.getInstance();

        // Only show manage to admins

        isAdmin = ThisUser.isAdmin();
        if (isAdmin){
            addEventButton.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        } else {
            addEventButton.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        chapterName = ThisUser.getChapterName();
        final ChapterEventAdapter.OnChapListener o = this;

        firestore.collection("Chapter").whereEqualTo("chapterName", chapterName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);

                chapterRef = ds.getReference();
                query = chapterRef.collection("ChapterEvent").orderBy("date");

                adapter = new ChapterEventAdapter(query, o);
                adapter.setQuery(query);

                eventRV.setLayoutManager(new LinearLayoutManager(getContext()));
                eventRV.setAdapter(adapter);
                eventRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            }
        });
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
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
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_add_chap:
                getFragmentManager().executePendingTransactions();
                if (!addEventDialog.isAdded()) {
                    addEventDialog.show(getFragmentManager(), "addEventDialog");
                }
                break;
        }
    }

    @Override
    public void onChapSelected(final DocumentSnapshot chapEvent) {
        if (!isAdmin) {

            DocumentReference userRef = FirebaseFirestore.getInstance().collection("DatabaseUser").document(
                    ThisUser.getUid()
            );


            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {

                        Map<String, Integer> eventsSignedUp = (Map<String, Integer>) task.getResult().get("chapterEvents");
                        String eventName = chapEvent.get("eventName").toString();
                        // Show the already signed up dialog
                        if (eventsSignedUp.containsKey(eventName)) {
                            boolean attendance = chapEvent.getBoolean("attendanceActive");
                            attendanceDialog.setAttendanceActive(attendance);
                            attendanceDialog.setEventName(chapEvent.get("eventName").toString());
                            if(attendance)attendanceDialog.setAttendancePassword(chapEvent.get("signInKey").toString());
                            getFragmentManager().executePendingTransactions();
                            if(!attendanceDialog.isAdded())
                                attendanceDialog.show(getFragmentManager(), "addOfficerDialog");

                        }

                        // Show the signed up dialog
                        else {
                            chapEventDialog.setEventName(eventName);

                            getFragmentManager().executePendingTransactions();
                            if (!chapEventDialog.isAdded())
                                chapEventDialog.show(getFragmentManager(), "ChapEventsDialog");

                        }
                    }
                }
            });
        } else {
            menuDialog.setChapterEventSnapshot(chapEvent);

            getFragmentManager().executePendingTransactions();
            if (!menuDialog.isAdded()) {
                menuDialog.show(getFragmentManager(), "editEventDialog");
            }
        }
    }
}
