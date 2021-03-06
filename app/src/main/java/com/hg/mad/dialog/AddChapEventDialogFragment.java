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
import com.hg.mad.model.ChapterEvent;
import com.hg.mad.ui.ChapterEventsFragment;
import com.hg.mad.util.ThisUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AddChapEventDialogFragment extends DialogFragment implements View.OnClickListener{
    private View rootView;
    private EditText nameEditText;
    private EditText dateEditText;
    private Spinner typeSpinner;
    private CheckBox attendanceCheckBox;
    private EditText passwordEditText;
    private EditText descriptionEditText;

    private Button add;
    private Button cancel;
    private ChapterEventsFragment chapterEventsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_add_chapter_event, container, false);

        nameEditText = rootView.findViewById(R.id.editText_name);
        dateEditText = rootView.findViewById(R.id.editText_date);
        typeSpinner = rootView.findViewById(R.id.typeSpinner2);
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

    public void setChapterEventsFragment(ChapterEventsFragment chapterEventsFragment) {
        this.chapterEventsFragment = chapterEventsFragment;
    }

    public void onAddClicked(){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        // Update Chapter
        chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);
                    final CollectionReference events = chapter.getReference().collection("ChapterEvent");

                    Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                    if(typeSpinner.getSelectedItemPosition()!=0) {
                        if (!currentEventsChap.containsKey(nameEditText.getText().toString())) {

                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                Map<String, Object> event = new HashMap<String, Object>();
                                        event.put("eventName",nameEditText.getText().toString());
                                        event.put("eventType", typeSpinner.getSelectedItem().toString());
                                        event.put("description", descriptionEditText.getText().toString());
                                        event.put("date", dateFormat.parse(dateEditText.getText().toString()));
                                        event.put("password", passwordEditText.getText().toString());
                                        event.put("attendanceActive",attendanceCheckBox.isChecked());
                                events.add(event).addOnSuccessListener(
                                        new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                chapterEventsFragment.query.get().addOnSuccessListener(
                                                        new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                System.out.println("RETURN"+queryDocumentSnapshots.getDocuments().size());
                                                            }
                                                        }
                                                );

                                            }
                                        }
                                );

                                currentEventsChap.put(nameEditText.getText().toString(), new HashMap<String, Attendee>());
                                chapter.getReference().update("chapterEvents", currentEventsChap);

                                Toast.makeText(getContext(), "Chapter event created", Toast.LENGTH_SHORT).show();
                                dismiss();

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

    private void reset(){
        nameEditText.setText("");
        dateEditText.setText("");
        attendanceCheckBox.setSelected(false);
        passwordEditText.setText("");
        descriptionEditText.setText("");
    }
}
