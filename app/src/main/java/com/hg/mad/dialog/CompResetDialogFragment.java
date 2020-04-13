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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.util.ThisUser;

import java.util.HashMap;
import java.util.Map;

 public class CompResetDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_comp_reset, container, false);

        rootView.findViewById(R.id.button_yes).setOnClickListener(this);
        rootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                onYesClicked();
                break;
            case R.id.button_cancel:
                onNoClicked();
                break;
        }
    }

    public void onYesClicked() {

        DocumentSnapshot user = ThisUser.getUserSnapshot();

        // Update Chapter
        FirebaseFirestore.getInstance().collection("Chapter").whereEqualTo("chapterName", user.get("chapterName"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                    chapter.getReference().update("competitiveEvents", new HashMap<String, Map<String, String>>());

                    Map<String, String> users = (Map<String, String>) chapter.get("usersInChapter");
                    for (String uid : users.keySet()){

                        DocumentReference userRef = FirebaseFirestore.getInstance().collection("DatabaseUser").document(uid);
                        userRef.update("competitiveEvents", new HashMap<String, Integer>());
                    }
                }
            }
        });

        dismiss();
        Toast.makeText(getContext(), "Events reset", Toast.LENGTH_SHORT).show();
    }

    public void onNoClicked() {
        dismiss();
    }
}
