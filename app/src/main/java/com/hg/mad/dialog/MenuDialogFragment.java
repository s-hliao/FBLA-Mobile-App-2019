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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.model.Attendee;
import com.hg.mad.model.ChapterEvent;
import com.hg.mad.util.ThisUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

 /**
 * Dialog Fragment containing filter form.
 */
public class MenuDialogFragment extends DialogFragment implements View.OnClickListener {
    private LinearLayout manageEvent;
    private LinearLayout signIn;
    private Button cancel;

    private ChapEventDialogFragment chapEventDialog;
    private TakeAttendanceDialogFragment  takeAttendanceDialog;
    private EditChapEventDialogFragment editChapEventDialog;

    private DocumentSnapshot chapterEventSnapshot;


    private View rootView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_menu, container, false);

        takeAttendanceDialog = new TakeAttendanceDialogFragment();
        editChapEventDialog = new EditChapEventDialogFragment();
        chapEventDialog = new ChapEventDialogFragment();


        manageEvent = rootView.findViewById(R.id.layout_edit_event);
        signIn = rootView.findViewById(R.id.layout_sign_up);
        cancel = rootView.findViewById(R.id.button_cancel3);


        if(ThisUser.isAdmin()){
            manageEvent.setVisibility(View.VISIBLE);
        } else{
            manageEvent.setVisibility(View.GONE);
        }


        cancel.setOnClickListener(this);
        manageEvent.setOnClickListener(this);
        signIn.setOnClickListener(this);

        return rootView;
    }

     public void setChapterEventSnapshot(DocumentSnapshot chapterEventSnapshot) {
         this.chapterEventSnapshot = chapterEventSnapshot;
     }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_edit_event:
                onEditClicked();
                break;
            case R.id.layout_sign_up:
                onSignInClicked();
                break;
            case R.id.button_cancel3:
                onCancelClicked();
                break;
        }
    }

     @Override
     public void onResume() {
         super.onResume();
         getDialog().getWindow().setLayout(
                 ViewGroup.LayoutParams.MATCH_PARENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
     }

     public void onEditClicked(){
        editChapEventDialog.setChapterEventSnapshot(chapterEventSnapshot);
         getFragmentManager().executePendingTransactions();
         if(!editChapEventDialog.isAdded())
             editChapEventDialog.show(getFragmentManager(), "addOfficerDialog");

        dismiss();
     }

     public void onSignInClicked(){
         DocumentReference userRef = FirebaseFirestore.getInstance().collection("DatabaseUser").document(
                 ThisUser.getUid()
         );


         userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful() && task.getResult() != null) {

                     Map<String, Integer> eventsSignedUp = (Map<String, Integer>) task.getResult().get("chapterEvents");
                     String eventName = chapterEventSnapshot.get("eventName").toString();
                     // Show the already signed up dialog
                     if (eventsSignedUp.containsKey(eventName)) {
                         boolean attendance = chapterEventSnapshot.getBoolean("attendanceActive");
                         takeAttendanceDialog.setAttendanceActive(attendance);
                         takeAttendanceDialog.setEventName(chapterEventSnapshot.get("eventName").toString());
                         if(attendance)takeAttendanceDialog.setAttendancePassword(chapterEventSnapshot.get("signInKey").toString());
                         getActivity().getSupportFragmentManager().executePendingTransactions();
                         if(!takeAttendanceDialog.isAdded())
                             takeAttendanceDialog.show(getActivity().getSupportFragmentManager(), "addOfficerDialog");

                     }

                     // Show the signed up dialog
                     else {
                         chapEventDialog.setEventName(eventName);

                         getActivity().getSupportFragmentManager().executePendingTransactions();
                         if (!chapEventDialog.isAdded())
                             chapEventDialog.show(getActivity().getSupportFragmentManager(), "ChapEventDialog");

                     }
                 }
             }
         });



        dismiss();
     }

    public void onCancelClicked() {

        dismiss();
    }


}
