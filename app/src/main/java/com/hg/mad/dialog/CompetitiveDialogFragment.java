 package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hg.mad.R;

import org.w3c.dom.Text;

 public class CompetitiveDialogFragment extends DialogFragment implements View.OnClickListener {

    View rootView;
    String eventName;
    TextView signUp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_competitive, container, false);

        rootView.findViewById(R.id.button_yes).setOnClickListener(this);
        rootView.findViewById(R.id.button_no).setOnClickListener(this);

        signUp = rootView.findViewById(R.id.competitive_signup);
        signUp.setText("Sign up for "+ eventName);

        return rootView;
    }

    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                onYesClicked();
                break;
            case R.id.button_no:
                onNoClicked();
                break;
        }
    }

    public void onYesClicked() {

    }

    public void onNoClicked() {
        dismiss();
    }
}
