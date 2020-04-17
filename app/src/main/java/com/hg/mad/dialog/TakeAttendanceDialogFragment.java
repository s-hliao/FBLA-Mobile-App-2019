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

import com.hg.mad.R;

public class TakeAttendanceDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView eventNameText;
    private TextView password;
    private EditText typePassword;
    private Button no;
    private Button yes;

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
        no = rootView.findViewById(R.id.button_no);
        yes = rootView.findViewById(R.id.button_yes);

        if(attendanceActive){
            password.setVisibility(View.GONE);
            typePassword.setText(View.GONE);
        } else{
            password.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
        }

        eventNameText.setText(eventName);

        yes.setOnClickListener(this);
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
            case R.id.button_no:
                dismiss();
                break;
            case R.id.button_yes:
                onSignInClicked();
                break;
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
