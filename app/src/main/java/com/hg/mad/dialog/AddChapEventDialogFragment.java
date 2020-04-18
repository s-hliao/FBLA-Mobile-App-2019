package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.hg.mad.model.ChapterEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AddChapEventDialogFragment extends DialogFragment implements View.OnClickListener{
    private View rootView;
    private EditText nameEditText;
    private EditText dateEditText;
    private EditText typeEditText;
    private CheckBox attendanceCheckBox;
    private EditText passwordEditText;
    private EditText descriptionEditText;

    private Button add;
    private Button cancel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_add_chapter_event, container, false);

        nameEditText = rootView.findViewById(R.id.editText_name);
        dateEditText = rootView.findViewById(R.id.editText_date);
        typeEditText = rootView.findViewById(R.id.editText_type);
        attendanceCheckBox = rootView.findViewById(R.id.checkbox_attendance);
        passwordEditText = rootView.findViewById(R.id.editText_password);
        descriptionEditText = rootView.findViewById(R.id.editText_description);
        add = rootView.findViewById(R.id.button_cancel);
        cancel = rootView.findViewById(R.id.button_add);

        attendanceCheckBox.setOnClickListener(this);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        reset();

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
            case R.id.button_add:
                onAddClicked();
                break;
            case R.id.checkbox_attendance:
                onAttendanceClicked();
                break;
        }
    }

    public void onAttendanceClicked(){
        if(attendanceCheckBox.isChecked()){
            passwordEditText.setVisibility(View.VISIBLE);
        } else{
            passwordEditText.setVisibility(View.INVISIBLE);
        }
    }

    public void onAddClicked(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

        CollectionReference usersCollection = fireStore.collection("DatabaseUser");
        final DocumentReference userRef = usersCollection.document(currentUser.getUid());

        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    // Update DatabaseUser
                    DocumentSnapshot user = task.getResult();
                    Map<String, Integer> currentEventsUser = (Map<String, Integer>) user.get("chapterEvents");
                    currentEventsUser.put(nameEditText.toString(), 1);
                    userRef.update("chapterEvents", currentEventsUser);

                    // Update Chapter
                    chaptersCollection.whereEqualTo("chapterName", user.get("chapterName"))
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                                if (!currentEventsChap.containsKey(nameEditText.toString())) {
                                    currentEventsChap.put(nameEditText.toString(), new HashMap<String, Attendee>());

                                    SimpleDateFormat dateFormat= new SimpleDateFormat("MM/DD/yyyy");

                                    try {
                                        ChapterEvent event  = new ChapterEvent(nameEditText.getText().toString(),
                                                typeEditText.getText().toString(), descriptionEditText.getText().toString(),
                                                dateFormat.parse(dateEditText.getText().toString()),
                                                passwordEditText.getText().toString(),
                                                attendanceCheckBox.isChecked());

                                        currentEventsChap.get(nameEditText.toString()).put(currentUser.getUid(), new Attendee(currentUser.getDisplayName(), false));
                                        chapter.getReference().update("competitiveEvents", currentEventsChap);
                                        chapter.getReference().collection("ChapterEvent").add(event);
                                        Toast.makeText(getContext(), "Chapter event created", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    } catch (ParseException e) {
                                        Toast.makeText(getContext(), "Incorrect date format", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void onCancelClicked(){
        dismiss();
    }

    private void reset(){
        nameEditText.setText("");
        dateEditText.setText("");
        typeEditText.setText("");
        attendanceCheckBox.setSelected(false);
        passwordEditText.setText("");
        descriptionEditText.setText("");
    }
}
