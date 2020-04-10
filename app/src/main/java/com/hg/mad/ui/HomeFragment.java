package com.hg.mad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hg.mad.AuthUiActivity;
import com.hg.mad.R;
import com.hg.mad.adapter.MyCompEventAdapter;
import com.hg.mad.dialog.CompSUDialogFragment;
import com.hg.mad.model.DatabaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements
        MyCompEventAdapter.OnMyCompListener {

    private View root;
    private ViewFlipper viewFlipper;
    private TextView myChapEvents;
    private TextView myCompEvents;
    private boolean isChap;
    private boolean start;

    private RecyclerView myCompRecycler;
    private LinearLayoutManager layoutManager;
    private MyCompEventAdapter adapter;
    private List<String> myComp;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_home, container, false);

        // View flipping
        viewFlipper = root.findViewById(R.id.home_flipper);

        myChapEvents = root.findViewById(R.id.my_chap_events);
        myCompEvents = root.findViewById(R.id.my_comp_events);
        flipperOn(myChapEvents);
        flipperOff(myCompEvents);

        start = true;
        isChap = true;

        myChapEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start) {
                    flipperOn(myChapEvents);
                    flipperOff(myCompEvents);
                    chapterEvents();
                    isChap = true;
                    start = false;
                } else if (!isChap && viewFlipper.getInAnimation().hasEnded()) {
                    flipperOn(myChapEvents);
                    flipperOff(myCompEvents);
                    chapterEvents();
                    isChap = true;
                }
            }
        });

        myCompEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start){
                    flipperOn(myCompEvents);
                    flipperOff(myChapEvents);
                    competitiveEvents();
                    isChap = false;
                    start = false;
                } else if (isChap && viewFlipper.getInAnimation().hasEnded()) {
                    flipperOn(myCompEvents);
                    flipperOff(myChapEvents);
                    competitiveEvents();
                    isChap = false;
                }
            }
        });

        // Recycler Views
        myComp = new ArrayList<>();
        myCompRecycler = root.findViewById(R.id.recycler_my_comp);
        layoutManager = new LinearLayoutManager(getContext());
        myCompRecycler.setLayoutManager(layoutManager);
        myCompRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        updateCompAdapter();
        beganUpdating();

        return root;
    }

    private void beganUpdating() {
        DocumentReference databaseUserRef = FirebaseFirestore.getInstance().collection("DatabaseUser")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Set up competitive events listener
        databaseUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists() && snapshot.get("competitiveEvents") != null) {
                    myComp = new ArrayList<>();
                    Map<String, Integer> events = (Map<String, Integer>) snapshot.get("competitiveEvents");
                    myComp.addAll(events.keySet());

                    updateCompAdapter();
                }
            }
        });
    }

    private void updateCompAdapter(){
        adapter = new MyCompEventAdapter(myComp, this);
        myCompRecycler.setAdapter(adapter);
    }

    private void chapterEvents() {
        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
        viewFlipper.setDisplayedChild(0);
    }

    private void competitiveEvents() {
        viewFlipper.setInAnimation(getContext(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getContext(), R.anim.slide_out_left);
        viewFlipper.setDisplayedChild(1);
    }

    private void flipperOn(TextView v){
        v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        v.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        v.setElevation(0);
    }

    private void flipperOff(TextView v){
        v.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.background_light));
        v.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        v.setElevation(16);
    }

    @Override
    public void onMyCompSelected(String eventName) {

        CompSUDialogFragment compSUDialog = new CompSUDialogFragment();
        compSUDialog.setEventName(eventName);

        getFragmentManager().executePendingTransactions();
        if (!compSUDialog.isAdded())
            compSUDialog.show(getFragmentManager(), "CompSUDialog");
    }
}
