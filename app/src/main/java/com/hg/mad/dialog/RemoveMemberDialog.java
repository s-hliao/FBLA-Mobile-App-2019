package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.util.ThisUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RemoveMemberDialog extends DialogFragment implements View.OnClickListener {
    private Button yes;
    private Button no;
    private TextView name;

    private DocumentSnapshot member;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_remove_member, container, false);

        yes = root.findViewById(R.id.button_yes);
        no = root.findViewById(R.id.button_cancel);
        name = root.findViewById(R.id.text_name);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        name.setText(member.get("name").toString());

        return root;
    }

    public void setMember (DocumentSnapshot member){
        this.member = member;
    }

    @Override
    public void onResume() {
        super.onResume();
        name.setText(member.get("name").toString());

        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                onRemoveClicked();
                break;
            case R.id.button_cancel:
                onCancelClicked();;
                break;
        }
    }

    public void onRemoveClicked(){
        if (! (Boolean) member.get("isAdmin")) {

            // Update Chapter
            CollectionReference chaptersCollection = FirebaseFirestore.getInstance().collection("Chapter");
            chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                        Map<String, Map<String, String>> currentEventsChap = (Map<String, Map<String, String>>) chapter.get("competitiveEvents");
                        for (Map<String, String> event : currentEventsChap.values()){
                            if (event.containsKey(member.getId())){
                                event.remove(member.getId());
                            }
                        }

                        ArrayList<String> keys = new ArrayList<>();
                        for(String key : currentEventsChap.keySet()){
                            if (currentEventsChap.get(key).isEmpty()){
                                keys.add(key);
                            }
                        }
                        for (String key : keys){
                            currentEventsChap.remove(key);
                        }

                        chapter.getReference().update("competitiveEvents", currentEventsChap);

                        DocumentReference userRef = FirebaseFirestore.getInstance().collection("DatabaseUser").document(member.getId());
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("inChapter", false);
                        updates.put("isAdmin", false);
                        updates.put("chapterName", "");
                        updates.put("competitiveEvents", new HashMap<String, Integer>());
                        updates.put("chapterEvents", new HashMap<String, Integer>());

                        userRef.update(updates);
                    }
                }
            });

        } else {
            Toast.makeText(getContext(), "The admin cannot be removed", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    public void onCancelClicked(){
        dismiss();
    }

}