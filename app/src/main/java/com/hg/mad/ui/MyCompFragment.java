package com.hg.mad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hg.mad.R;
import com.hg.mad.adapter.MyCompEventAdapter;
import com.hg.mad.dialog.CompSUDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyCompFragment extends Fragment
        implements MyCompEventAdapter.OnMyCompListener {

    private View root;

    private RecyclerView myCompRecycler;
    private LinearLayoutManager layoutManager;
    private MyCompEventAdapter adapter;
    private List<String> myComp;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_mycomp, container, false);

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

    @Override
    public void onMyCompSelected(String eventName) {

        CompSUDialogFragment compSUDialog = new CompSUDialogFragment();
        compSUDialog.setEventName(eventName);

        getFragmentManager().executePendingTransactions();
        if (!compSUDialog.isAdded())
            compSUDialog.show(getFragmentManager(), "CompSUDialog");
    }
}
