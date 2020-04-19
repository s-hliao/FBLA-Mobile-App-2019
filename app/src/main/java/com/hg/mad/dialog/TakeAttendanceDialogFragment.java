package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class TakeAttendanceDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView title;
    private TextView password;
    private TextView signUpText;
    private TextView signInText;
    private EditText typePassword;
    private Button no;
    private Button remove;
    private Button signIn;
    private LinearLayout passwordContainer;

    private boolean attendanceActive;
    private String attendancePassword;
    private String eventName;

    View rootView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_take_attendance, container, false);

        title = rootView.findViewById(R.id.event_name_text);
        password = rootView.findViewById(R.id.password_text);
        typePassword = rootView.findViewById(R.id.password_type);
        no = rootView.findViewById(R.id.button_cancel_no);
        remove = rootView.findViewById(R.id.button_cancel_yes);
        signIn = rootView.findViewById(R.id.sign_in);
        passwordContainer = rootView.findViewById(R.id.password_container);
        signInText = rootView.findViewById(R.id.event_name_text);
        signUpText = rootView.findViewById(R.id.competitive_signedup3);

        signInText.setText("Sign In to "+eventName);
        signUpText.setText("Cancel Sign Up for "+eventName);

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
                break;
            case R.id.button_cancel_yes:
                onRemoveClicked();
                break;
        }
    }

    public void onCancelClicked(){dismiss();}

    public void onSignInClicked(){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = fireStore.collection("DatabaseUser");
        final DocumentReference userRef = usersCollection.document(ThisUser.getUid());
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        if(!attendanceActive||typePassword.getText().toString().equals(attendancePassword)){

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        // Update Chapter
                        chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                    Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                                    currentEventsChap.get(eventName).put(ThisUser.getUid(), new Attendee(ThisUser.getDisplayName(), true));
                                    chapter.getReference().update("chapterEvents", currentEventsChap);

                                    Toast.makeText(getContext(), "Signed in", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }
                        });
                    }
                }
            });

        } else{
            Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRemoveClicked(){

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = fireStore.collection("DatabaseUser");
        final DocumentReference userRef = usersCollection.document(ThisUser.getUid());
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                Map<String, Map<String, Attendee>> currentEventsChap = (Map) chapter.get("chapterEvents");
                                currentEventsChap.get(eventName).remove(ThisUser.getUid());

                                if(currentEventsChap.get(eventName).isEmpty()){
                                    currentEventsChap.remove(eventName);
                                }

                                chapter.getReference().update("chapterEvents", currentEventsChap);
                                dismiss();
                            }
                        }
                    });
                }
            }
        });
        Toast.makeText(getContext(), "Removed signup", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        signInText.setText("Sign In to "+eventName);
        signUpText.setText("Cancel "+eventName+" sign up");

        updateVisibility();

        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void updateVisibility(){

        if(!attendanceActive){
            passwordContainer.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);

        } else{
            passwordContainer.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
        }
    }
}
