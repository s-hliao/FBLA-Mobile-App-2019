package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.Query;
import com.hg.mad.R;
import com.hg.mad.util.ThisUser;

import java.util.HashMap;
import java.util.Map;

public class RemoveAllMembersDialog extends DialogFragment implements View.OnClickListener{
    private View root;
    private Button yes;
    private Button no;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_remove_all_members, container, false);

        yes = root.findViewById(R.id.button_yes);
        no = root.findViewById(R.id.button_cancel);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        return root;
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


        FirebaseFirestore.getInstance().collection("DatabaseUser")
        .whereEqualTo("chapterName", ThisUser.getChapterName()).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("inChapter", false);
                    updates.put("isAdmin", false);
                    updates.put("chapterName", null);
                   for(DocumentSnapshot ds:queryDocumentSnapshots){
                       if(!(boolean)ds.get("isAdmin")) ds.getReference().update(updates);

                   }
                   dismiss();
                }
            });


    }

    public void onCancelClicked(){
        dismiss();
    }
}
