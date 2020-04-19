package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.SignedInActivity;
import com.hg.mad.model.Attendee;
import com.hg.mad.util.ThisUser;

import java.util.HashMap;
import java.util.Map;

public class AllChapEventDialog extends DialogFragment implements View.OnClickListener{
    private View root;
    private Button resetAll;
    private Button no;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_chap_event_all, container, false);

        resetAll = root.findViewById(R.id.button_yes);
        no = root.findViewById(R.id.button_cancel);

        resetAll.setOnClickListener(this);
        no.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                onResetClicked();
                break;
            case R.id.button_cancel:
                onCancelClicked();;
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
                        Map<String, Map<String,Attendee>>updates = new HashMap<>();
                        for(String s:attendees.keySet()){
                            updates.put(s, new HashMap<String, Attendee>());
                        }
                        chapter.getReference().update("chapterEvents", updates);
                    }
                });
        Toast.makeText(getContext(), "Attendees Removed", Toast.LENGTH_SHORT).show();
        dismiss();

    }


    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onCancelClicked(){
        dismiss();
    }
}
