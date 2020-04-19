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
import com.hg.mad.ManageChapActivity;
import com.hg.mad.ManageCompActivity;
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
import com.hg.mad.model.Attendee;
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
    private View manageEventButton;
    private View divider;
    private View dividerr;

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
        manageEventButton = root.findViewById(R.id.button_manage_chap);
        divider = root.findViewById(R.id.divider5);
        dividerr = root.findViewById(R.id.divider3);

        addEventButton.setOnClickListener(this);
        manageEventButton.setOnClickListener(this);

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
            dividerr.setVisibility(View.VISIBLE);
        } else {
            addEventButton.setVisibility(View.GONE);

            divider.setVisibility(View.GONE);
            dividerr.setVisibility(View.VISIBLE);
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
            case R.id.button_manage_chap:
                startActivity(ManageChapActivity.createIntent(getContext()));
        }
    }

    @Override
    public void onChapSelected(final DocumentSnapshot chapEvent) {
        if (!isAdmin) {

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

        } else {

            menuDialog.setChapterEventSnapshot(chapEvent);
            getFragmentManager().executePendingTransactions();
            if (!menuDialog.isAdded()) {
                menuDialog.show(getFragmentManager(), "editEventDialog");
            }

        }
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
