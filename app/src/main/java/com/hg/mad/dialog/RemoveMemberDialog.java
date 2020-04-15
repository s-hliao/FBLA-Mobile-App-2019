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

import com.google.firebase.firestore.DocumentSnapshot;
import com.hg.mad.R;

import java.util.HashMap;
import java.util.Map;

public class RemoveMemberDialog extends DialogFragment implements View.OnClickListener {
    private Button yes;
    private Button no;
    private TextView name;

    private DocumentSnapshot member;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_remove_member, container, false);

        yes = root.findViewById(R.id.button_yes);
        no = root.findViewById(R.id.button_cancel);
        name = root.findViewById(R.id.text_name);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        name.setText(member.get("name").toString());

        return root;
    }

    public void setMember (DocumentSnapshot member){
        this.member = member;
    }

    @Override
    public void onResume() {
        super.onResume();
        name.setText(member.get("name").toString());

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
                onCancelClicked();;
                break;
        }
    }

    public void onRemoveClicked(){
        if (! (Boolean) member.get("isAdmin")) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("inChapter", false);
            updates.put("isAdmin", false);
            updates.put("chapterName", "");
            member.getReference().update(updates);
        } else {
            Toast.makeText(getContext(), "The admin cannot be removed", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    public void onCancelClicked(){
        dismiss();
    }

}
