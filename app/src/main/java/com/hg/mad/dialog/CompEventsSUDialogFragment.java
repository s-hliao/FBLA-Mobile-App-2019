 package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hg.mad.R;
import com.hg.mad.adapter.CompSUAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

 public class CompEventsSUDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView eventRecycler;
    private TextView nameView;
    private String name;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_compevent_su, container, false);
        rootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        eventRecycler = rootView.findViewById(R.id.recycler_event);

        nameView = rootView.findViewById(R.id.comp_name);
        nameView.setText("Sign ups for "+ name);

        rootView.findViewById(R.id.recycler_event);

        getEvents();

        return rootView;
    }

    public void setName(String name){
        this.name = name;
    }

     public void setId(String id) {
         this.id = id;
     }

     @Override
     public void onResume() {
         super.onResume();

         getDialog().getWindow().setLayout(
                 ViewGroup.LayoutParams.MATCH_PARENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
     }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void getEvents() {

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        CollectionReference userCollection = fireStore.collection("DatabaseUser");
        final DocumentReference userRef = userCollection.document(id);

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists() && snapshot.get("competitiveEvents") != null) {
                    DocumentSnapshot user = snapshot;

                    Map<String, Integer> events = (Map<String, Integer>) user.get("competitiveEvents");


                    if (events != null) {
                        List eventsSU = events.keySet().stream().collect(Collectors.toList());

                        CompSUAdapter adapter = new CompSUAdapter(eventsSU);
                        eventRecycler.setAdapter(adapter);
                        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

                    } else {
                        CompSUAdapter adapter = new CompSUAdapter(new ArrayList<String>());
                        eventRecycler.setAdapter(adapter);
                        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }
        });
    }

    public void onCancelClicked() {
        dismiss();
    }
}
