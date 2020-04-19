package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.model.Attendee;
import com.hg.mad.util.ThisUser;

import java.util.HashMap;
import java.util.Map;

public class ChapEventDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView;
    private String eventName;
    private TextView signUp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_competitive, container, false);

        rootView.findViewById(R.id.button_yes).setOnClickListener(this);
        rootView.findViewById(R.id.button_no).setOnClickListener(this);

        signUp = rootView.findViewById(R.id.competitive_signup);
        signUp.setText("Sign up for "+ eventName);

        return rootView;
    }

    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    @Override
    public void onResume() {
        super.onResume();
        signUp.setText("Sign up for "+ eventName);

        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                onYesClicked();
                break;
            case R.id.button_no:
                onNoClicked();
                break;
        }
    }

    public void onYesClicked() {

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = fireStore.collection("DatabaseUser");
        final DocumentReference userRef = usersCollection.document(ThisUser.getUid());
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot ss = task.getResult();
                    Map<String, Integer> events = (Map) ss.get("chapterEvents");
                    events.put(eventName, 1);
                    userRef.update("chapterEvents", events);

                    // Update Chapter
                    chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                                if (!currentEventsChap.containsKey(eventName)) {
                                    currentEventsChap.put(eventName, new HashMap<String, Attendee>());
                                }
                                currentEventsChap.get(eventName).put(ThisUser.getUid(),
                                        new Attendee(ThisUser.getDisplayName(), false));

                                chapter.getReference().update("chapterEvents", currentEventsChap);
                            }
                        }
                    });
                }
            }
        });

        dismiss();
        Toast.makeText(getContext(), "Signed up", Toast.LENGTH_SHORT).show();
    }

    public void onNoClicked() {
        dismiss();
    }
}
