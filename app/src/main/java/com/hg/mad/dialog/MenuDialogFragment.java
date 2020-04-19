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
    private LinearLayout manageSignups;
    private Button cancel;

    private ChapEventDialogFragment chapEventDialog;
    private TakeAttendanceDialogFragment  takeAttendanceDialog;
    private EditChapEventDialogFragment editChapEventDialog;
    private ChapEventManageDialog manageSignupsDialog;

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
        manageSignupsDialog = new ChapEventManageDialog();

        manageEvent = rootView.findViewById(R.id.layout_edit_event);
        signIn = rootView.findViewById(R.id.layout_sign_up);
        cancel = rootView.findViewById(R.id.button_cancel3);
        manageSignups = rootView.findViewById(R.id.layout_manage);

        cancel.setOnClickListener(this);
        manageEvent.setOnClickListener(this);
        signIn.setOnClickListener(this);
        manageSignups.setOnClickListener(this);

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
            case R.id.layout_manage:
                onManageClicked();
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

     private void onEditClicked(){
         editChapEventDialog.setChapterEventSnapshot(chapterEventSnapshot);
         getFragmentManager().executePendingTransactions();
         if(!editChapEventDialog.isAdded())
             editChapEventDialog.show(getFragmentManager(), "addOfficerDialog");

        dismiss();
     }

     private void onSignInClicked(){

         FirebaseFirestore.getInstance().collection("Chapter")
                 .whereEqualTo("chapterName", ThisUser.getChapterName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 DocumentSnapshot chapRef = queryDocumentSnapshots.getDocuments().get(0);
                 Map<String, Map<String, Map<String, Object>>> events = (Map) chapRef.get("chapterEvents");
                 String eventName = (String) chapterEventSnapshot.get("eventName");

                 if (events.containsKey(eventName) && events.get(eventName).containsKey(ThisUser.getUid())) {
                     boolean attendance = chapterEventSnapshot.getBoolean("attendanceActive");

                     takeAttendanceDialog.setAttendanceActive(attendance);
                     takeAttendanceDialog.setEventName(chapterEventSnapshot.get("eventName").toString());
                     if (attendance)
                         takeAttendanceDialog.setAttendancePassword(chapterEventSnapshot.get("signInKey").toString());

                     showAttendance();
                 }

                 // Show the signed up dialog
                 else {
                     chapEventDialog.setEventName(eventName);
                     showChapEvent();
                     dismiss();
                 }
             }
         });
     }

    private void showAttendance(){

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        final CollectionReference chaptersCollection = fireStore.collection("Chapter");

        // Update Chapter
        chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                    Map<String, Map<String, Map<String, Object>>> currentEventsChap = (Map) chapter.get("chapterEvents");
                    Map<String, Object> user = currentEventsChap.get(chapterEventSnapshot.get("eventName")).get(ThisUser.getUid());
                    if ((boolean) user.get("signedIn")) {
                        takeAttendanceDialog.setAttendanceActive(false);
                    }

                    getFragmentManager().executePendingTransactions();
                    if(!takeAttendanceDialog.isAdded())
                        takeAttendanceDialog.show(getFragmentManager(), "takeAttendanceDialog");

                    dismiss();
                }
            }
        });
    }

    private void showChapEvent(){
        getFragmentManager().executePendingTransactions();
        if(!chapEventDialog.isAdded())
            chapEventDialog.show(getFragmentManager(), "chapEventDialog");
    }

    public void onManageClicked(){
        manageSignupsDialog.setEventName(chapterEventSnapshot.get("eventName").toString());
        getFragmentManager().executePendingTransactions();
        if(!manageSignupsDialog.isAdded())
            manageSignupsDialog.show(getFragmentManager(), "managedSignupsDialog");
        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }


}
