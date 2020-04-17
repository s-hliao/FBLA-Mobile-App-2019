package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Map;

public class TakeAttendanceDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView eventNameText;
    private TextView password;
    private TextView cancelSignUp;
    private EditText typePassword;
    private Button no;
    private Button remove;
    private Button signIn;

    private boolean attendanceActive;
    private String attendancePassword;
    private String eventName;

    View rootView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_take_attendance, container, false);

        eventNameText = rootView.findViewById(R.id.event_name_text);
        password =rootView.findViewById(R.id.password_text);
        typePassword = rootView.findViewById(R.id.password_type);
        no = rootView.findViewById(R.id.button_cancel_no);
        remove = rootView.findViewById(R.id.button_cancel_yes);
        signIn = rootView.findViewById(R.id.sign_in);
        cancelSignUp = rootView.findViewWithTag(R.id.competitive_signedup3);

        if(attendanceActive){
            password.setVisibility(View.GONE);
            typePassword.setText(View.GONE);
        } else{
            password.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
        }

        eventNameText.setText(eventName);

        signIn.setOnClickListener(this);
        remove.setOnClickListener(this);
        no.setOnClickListener(this);

        return rootView;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setAttendanceActive(boolean attendanceActive) {
        this.attendanceActive = attendanceActive;
    }

    public void setAttendancePassword(String attendancePassword) {
        this.attendancePassword = attendancePassword;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel_no:
                onCancelClicked();
                break;
            case R.id.sign_in:
                onSignInClicked();
                dismiss();
                break;
            case R.id.button_cancel_yes:
                onRemoveClicked();
                dismiss();
        }
    }

    public void onCancelClicked(){dismiss();}

    public void onSignInClicked(){
        if(!attendanceActive||typePassword.getText().toString().equals(attendancePassword)){

            Toast.makeText(getContext(), "Signed In", Toast.LENGTH_SHORT).show();
            dismiss();
        } else{
            Toast.makeText(getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
        }

    }

    public void onRemoveClicked(){

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
                    currentEventsUser.remove(eventName);
                    userRef.update("chapterEvents", currentEventsUser);

                    // Update Chapter
                    chaptersCollection.whereEqualTo("chapterName", user.get("chapterName"))
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                                currentEventsChap.get(eventName).remove(currentUser.getUid());

                                chapter.getReference().update("chapterEvents", currentEventsChap);
                            }
                        }
                    });
                }
            }
        });
        Toast.makeText(getContext(), "Removed Signup", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(attendanceActive){
            password.setVisibility(View.GONE);
            typePassword.setText(View.GONE);
        } else{
            password.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
        }
    }
}
