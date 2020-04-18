package com.hg.mad.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.SignedInActivity;
import com.hg.mad.util.ThisUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Signed;

public class RemoveMemberDialog extends DialogFragment implements View.OnClickListener {
    private Button yes;
    private Button no;
    private Button elevate;
    private TextView elevateText;

    private DocumentSnapshot member;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_remove_member, container, false);

        yes = root.findViewById(R.id.button_yes);
        no = root.findViewById(R.id.button_cancel);
        elevate = root.findViewById(R.id.button_elevate);
        elevateText = root.findViewById(R.id.elevate_text);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        elevate.setOnClickListener(this);

        if((boolean)member.get("isAdmin")){
            elevateText.setVisibility(View.GONE);
            elevate.setVisibility(View.GONE);
        } else{
            elevateText.setVisibility(View.VISIBLE);
            elevate.setVisibility(View.VISIBLE);
        }

        return root;
    }

    public void setMember (DocumentSnapshot member){
        this.member = member;
    }

    @Override
    public void onResume() {
        super.onResume();

        if((boolean)member.get("isAdmin")){
            elevateText.setVisibility(View.GONE);
            elevate.setVisibility(View.GONE);
        } else{
            elevateText.setVisibility(View.VISIBLE);
            elevate.setVisibility(View.VISIBLE);
        }

        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                onRemoveClicked();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
            case R.id.button_elevate:
                onElevateClicked();
                break;
        }
    }

    public void onElevateClicked(){
        member.getReference().update("isAdmin", true);
        Toast.makeText(getContext(), "Elevated to admin", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    public void onRemoveClicked(){
        if (! (Boolean) member.get("isAdmin")) {
            ((SignedInActivity) getActivity()).leaveUpdate(member.getId(), false);
        } else {
            Toast.makeText(getContext(), "An admin cannot be removed", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    public void onCancelClicked(){
        dismiss();
    }

}
