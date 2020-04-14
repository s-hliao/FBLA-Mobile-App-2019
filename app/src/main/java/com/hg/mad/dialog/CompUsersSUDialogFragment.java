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

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.hg.mad.adapter.CompSUAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

 public class CompUsersSUDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView;
    private String eventName;
    private RecyclerView userRecycler;
    private TextView signUp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_compuser_su, container, false);
        rootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        userRecycler = rootView.findViewById(R.id.recycler_user);

        signUp = rootView.findViewById(R.id.competitive_signup);
        signUp.setText("Sign ups for "+ eventName);

        rootView.findViewById(R.id.recycler_user);

        getUsers();

        return rootView;
    }

    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void getUsers() {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

        CollectionReference userCollection = fireStore.collection("DatabaseUser");
        final DocumentReference userRef = userCollection.document(currentUser.getUid());

        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists() && snapshot.get("competitiveEvents") != null) {
                    DocumentSnapshot user = snapshot;

                    chaptersCollection.whereEqualTo("chapterName", user.get("chapterName"))
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                Map<String, Map<String, String>> currentEventsChap = (Map) chapter.get("competitiveEvents");

                                if (currentEventsChap != null && currentEventsChap.containsKey(eventName)) {
                                    Collection<String> users = currentEventsChap.get(eventName).values();
                                    List usersArray = users.stream().collect(Collectors.toList());

                                    CompSUAdapter adapter = new CompSUAdapter(usersArray);
                                    userRecycler.setAdapter(adapter);
                                    userRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                } else {
                                    CompSUAdapter adapter = new CompSUAdapter(new ArrayList<String>());
                                    userRecycler.setAdapter(adapter);
                                    userRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void onCancelClicked() {
        dismiss();
    }
}
