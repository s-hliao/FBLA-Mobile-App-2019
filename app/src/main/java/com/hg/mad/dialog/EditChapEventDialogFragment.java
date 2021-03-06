package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.hg.mad.model.Chapter;
import com.hg.mad.model.ChapterEvent;
import com.hg.mad.ui.ChapterEventsFragment;
import com.hg.mad.util.ThisUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditChapEventDialogFragment extends DialogFragment implements View.OnClickListener{
    private View rootView;
    private EditText nameEditText;
    private EditText dateEditText;
    private Spinner typeSpinner;
    private CheckBox attendanceCheckBox;
    private EditText passwordEditText;
    private EditText descriptionEditText;

    private Button add;
    private Button cancel;
    private Button remove;

    private DocumentSnapshot chapterEventSnapshot;

    private ChapterEventsFragment chapterEventsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_edit_chapter_event, container, false);

        nameEditText = rootView.findViewById(R.id.editText_name);
        dateEditText = rootView.findViewById(R.id.editText_date);
        typeSpinner = rootView.findViewById(R.id.typeSpinner3);
        attendanceCheckBox = rootView.findViewById(R.id.checkbox_attendance);
        passwordEditText = rootView.findViewById(R.id.editText_password);
        descriptionEditText = rootView.findViewById(R.id.editText_description);
        add = rootView.findViewById(R.id.button_cancel);
        cancel = rootView.findViewById(R.id.button_add);
        remove = rootView.findViewById(R.id.button_remove);

        attendanceCheckBox.setOnClickListener(this);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        remove.setOnClickListener(this);

        return rootView;
    }

    public void setChapterEventSnapshot(DocumentSnapshot chapterEventSnapshot) {
        this.chapterEventSnapshot = chapterEventSnapshot;
    }

    @Override
    public void onResume() {
        super.onResume();

        chapterEventSnapshot.getReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                ChapterEvent ds = documentSnapshot.toObject(ChapterEvent.class);
                nameEditText.setText(ds.getEventName());
                dateEditText.setText(dateFormat.format(ds.getDate()));

                switch(ds.getEventType()) {
                    case "Meeting":
                        typeSpinner.setSelection(1);
                        break;
                    case "Fundraiser":
                        typeSpinner.setSelection(2);
                        break;
                    case "Conference":
                        typeSpinner.setSelection(3);
                        break;
                    case "Community Service":
                        typeSpinner.setSelection(4);
                        break;
                    case "Other":
                        typeSpinner.setSelection(5);
                        break;
                }

                attendanceCheckBox.setChecked(ds.getAttendanceActive());
                if(ds.getAttendanceActive()){
                    passwordEditText.setText(ds.getSignInKey());
                    passwordEditText.setVisibility(View.VISIBLE);
                } else{
                    passwordEditText.setText("");
                    passwordEditText.setVisibility(View.INVISIBLE);
                }
                descriptionEditText.setText(ds.getDescription());
            }
        });

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
            case R.id.button_remove:
                onRemoveClicked();
                break;
        }
    }

    public void onRemoveClicked() {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("Chapter")
                .whereEqualTo("chapterName", ThisUser.getChapterName()).get(

        ).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot chapter = queryDocumentSnapshots.getDocuments().get(0);

                Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                currentEventsChap.remove(chapterEventSnapshot.get("eventName"));
                chapter.getReference().update("chapterEvents", currentEventsChap);
            }
        });

        chapterEventSnapshot.getReference().delete();
        Toast.makeText(getContext(), "Event Removed", Toast.LENGTH_SHORT).show();
        dismiss();


    }

    public void onAttendanceClicked(){
        if(attendanceCheckBox.isChecked()){
            passwordEditText.setVisibility(View.VISIBLE);
        } else{
            passwordEditText.setVisibility(View.INVISIBLE);
        }
    }

    public void setChapterEventsFragment(ChapterEventsFragment chapterEventsFragment) {
        this.chapterEventsFragment = chapterEventsFragment;
    }

    public void onAddClicked(){

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        final CollectionReference usersCollection = fireStore.collection("DatabaseUser");
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                    Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                    final String previousName = (String) chapterEventSnapshot.get("eventName") ;
                    final String newName = nameEditText.getText().toString();
                    if(typeSpinner.getSelectedItemPosition()!=0) {
                        if (previousName.equals(newName) || !currentEventsChap.containsKey(newName)) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                            try {

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("eventName", nameEditText.getText().toString());
                                updates.put("eventType", typeSpinner.getSelectedItem().toString());
                                updates.put("description", descriptionEditText.getText().toString());
                                updates.put("date", dateFormat.parse(dateEditText.getText().toString()));
                                updates.put("signInKey", passwordEditText.getText().toString());
                                updates.put("attendanceActive", attendanceCheckBox.isChecked());

                                Map<String, Attendee> attendees = currentEventsChap.remove(chapterEventSnapshot.get("eventName"));

                                chapterEventSnapshot.getReference().update(updates);

                                currentEventsChap.put(nameEditText.getText().toString(), attendees);
                                chapter.getReference().update("chapterEvents", currentEventsChap);

                                // Update all users who signed up for this event
                                if (previousName != newName) {
                                    usersCollection.whereEqualTo("chapterName", ThisUser.getChapterName()).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot s : ds) {
                                                        Map<String, Integer> events = (Map) s.get("chapterEvents");
                                                        if (events.containsKey(previousName)) {
                                                            events.remove(previousName);
                                                            events.put(newName, 1);
                                                        }
                                                        s.getReference().update("chapterEvents", events);
                                                    }
                                                }
                                            });
                                }

                                Toast.makeText(getContext(), "Chapter event updated", Toast.LENGTH_SHORT).show();
                                dismiss();
                                chapterEventsFragment.resetQuery();
                            } catch (ParseException e) {
                                Toast.makeText(getContext(), "Incorrect date format", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getContext(), "Chapter Event name cannot already exist", Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(getContext(), "Please Select an Event Type", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }

    public void onCancelClicked(){
        dismiss();
    }
}
