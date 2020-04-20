package com.hg.mad.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.hg.mad.dialog.AllChapEventDialog;
import com.hg.mad.dialog.ChapEventDialogFragment;
import com.hg.mad.dialog.ChapFilters;
import com.hg.mad.dialog.EditChapEventDialogFragment;
import com.hg.mad.dialog.EditOfficerDialogFragment;
import com.hg.mad.dialog.FilterChapDialogFragment;
import com.hg.mad.dialog.Filters;
import com.hg.mad.dialog.MenuDialogFragment;
import com.hg.mad.dialog.SocMediaDialogFragment;
import com.hg.mad.dialog.TakeAttendanceDialogFragment;
import com.hg.mad.model.Attendee;
import com.hg.mad.model.Chapter;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.model.Officer;
import com.hg.mad.util.ThisUser;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Signed;

public class ChapterEventsFragment extends Fragment implements
        View.OnClickListener,
        FilterChapDialogFragment.FilterListener,
        ChapterEventAdapter.OnChapListener{

    private LinearLayout addEventButton;
    private LinearLayout resetAllButton;
    private View divider2;

    private RecyclerView eventRV;

    private DocumentReference chapterRef;
    private FirebaseFirestore firestore;
    private Query query;
    private DocumentReference chapter;

    private ChapterEventAdapter adapter;

    private MenuDialogFragment menuDialog;
    private AddChapEventDialogFragment addEventDialog;
    private TakeAttendanceDialogFragment attendanceDialog;
    private ChapEventDialogFragment chapEventDialog;
    private AllChapEventDialog allChapEventDialog;

    private FilterChapDialogFragment filterDialog;

    private boolean isAdmin;
    private String chapterName;

    private ImageView filterButton;
    private ChapFilters filters;
    private Spinner typeSpinner;
    private String searchText;

    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapter_events, container, false);

        eventRV = root.findViewById(R.id.recycler_chap_events);
        addEventButton = root.findViewById(R.id.button_add_chap);
        resetAllButton = root.findViewById(R.id.btn_removeAll);
        divider2 = root.findViewById(R.id.divider2);

        addEventButton.setOnClickListener(this);
        resetAllButton.setOnClickListener(this);

        menuDialog = new MenuDialogFragment();
        addEventDialog = new AddChapEventDialogFragment();
        attendanceDialog = new TakeAttendanceDialogFragment();
        chapEventDialog = new ChapEventDialogFragment();
        allChapEventDialog = new AllChapEventDialog();
        filterDialog = new FilterChapDialogFragment();

        FirebaseFirestore.setLoggingEnabled(true);
        firestore = FirebaseFirestore.getInstance();

        filterButton = root.findViewById(R.id.button_filter);
        filterButton.setOnClickListener(this);
        filters = ChapFilters.getDefault();
        typeSpinner = root.findViewById(R.id.spinner3);
        initSearch();

        // Only show manage to admins

        isAdmin = ThisUser.isAdmin();
        if (isAdmin){
            addEventButton.setVisibility(View.VISIBLE);
            resetAllButton.setVisibility(View.VISIBLE);
            divider2.setVisibility(View.VISIBLE);
        } else {
            addEventButton.setVisibility(View.GONE);
            resetAllButton.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        }

        chapterName = ThisUser.getChapterName();
        final ChapterEventAdapter.OnChapListener o = this;

        firestore.collection("Chapter").whereEqualTo("chapterName", chapterName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);

                chapterRef = ds.getReference();
                query = chapterRef.collection("ChapterEvent").orderBy("date").orderBy("lower").orderBy("typeLower");

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

        onFilter(filters);
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

    private void hideKeyboard() {


        typeSpinner.clearFocus();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initSearch() {
        typeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchText = typeSpinner.getSelectedItem().toString();
                onFilter(filters);
            }
        });

    }

    @Override
    public void onFilter(final ChapFilters newfilters) {

        firestore.collection("Chapter").whereEqualTo("chapterName", chapterName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                chapter = queryDocumentSnapshots.getDocuments().get(0).getReference();
                filter(newfilters);
            }
        });
    }

    private void filter(final ChapFilters newfilters){

        chapter.collection("ChapterEvent").orderBy("date")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                if (!documents.isEmpty()) {
                    DocumentSnapshot first = documents.get(0);
                    DocumentSnapshot last = documents.get(documents.size() - 1);



                    Date startDate;
                    if (newfilters.hasStartDate()) {
                        startDate = newfilters.getStartDate();
                    } else {
                        startDate = ((com.google.firebase.Timestamp) first.get("date")).toDate();
                    }

                    Date endDate;
                    if (newfilters.hasEndDate()) {
                        endDate = newfilters.getEndDate();
                    } else {
                        endDate = ((com.google.firebase.Timestamp) last.get("date")).toDate();
                    }

                    query = chapter.collection("ChapterEvent");

                    if(!searchText.equals("") && searchText.equals(null)){
                        query = query.whereEqualTo("eventType", searchText);
                    }
                    query = query.orderBy("date")
                            .startAt(startDate)
                            .endAt(endDate);

                    // Update the query
                    adapter.setQuery(query);

                    // Save filters
                    filters = newfilters;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_add_chap:
                hideKeyboard();
                getFragmentManager().executePendingTransactions();
                if (!addEventDialog.isAdded()) {
                    addEventDialog.show(getFragmentManager(), "addEventDialog");
                }
                break;
            case R.id.btn_removeAll:
                hideKeyboard();
                getFragmentManager().executePendingTransactions();
                if (!allChapEventDialog.isAdded()) {
                    allChapEventDialog.show(getFragmentManager(), "allChapEventDialog");
                }
                break;
            case R.id.button_filter:
                hideKeyboard();
                getFragmentManager().executePendingTransactions();
                if (!filterDialog.isAdded()) {
                    filterDialog.show(getFragmentManager(), "filterDialog");
                }
                break;
        }
    }

    @Override
    public void onChapSelected(final DocumentSnapshot chapEvent) {

        hideKeyboard();

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
