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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hg.mad.R;

import java.util.HashMap;
import java.util.Map;

 /**
 * Dialog Fragment containing filter form.
 */
public class MenuDialogFragment extends DialogFragment implements View.OnClickListener {
    private LinearLayout manageEvent;
    private LinearLayout signIn;
    private Button cancel;

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


        manageEvent = rootView.findViewById(R.id.layout_edit_event);
        signIn = rootView.findViewById(R.id.layout_sign_up);
        cancel = rootView.findViewById(R.id.button_cancel3);


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
        editChapEventDialog.setChapterEventReference(chapterEventSnapshot.getReference());
         getFragmentManager().executePendingTransactions();
         if(!editChapEventDialog.isAdded())
             editChapEventDialog.show(getFragmentManager(), "addOfficerDialog");

        dismiss();
     }

     public void onSignInClicked(){
        boolean attendance = chapterEventSnapshot.getBoolean("attendanceActive");
         takeAttendanceDialog.setAttendanceActive(attendance);
         takeAttendanceDialog.setEventName(chapterEventSnapshot.get("eventName").toString());
         if(attendance)takeAttendanceDialog.setAttendancePassword(chapterEventSnapshot.get("signInKey").toString());
         getFragmentManager().executePendingTransactions();
         if(!takeAttendanceDialog.isAdded())
             takeAttendanceDialog.show(getFragmentManager(), "addOfficerDialog");
        dismiss();
     }

    public void onCancelClicked() {

        dismiss();
    }


}
