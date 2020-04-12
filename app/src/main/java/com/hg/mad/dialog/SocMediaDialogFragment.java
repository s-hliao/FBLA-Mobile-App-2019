 package com.hg.mad.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hg.mad.R;
import com.hg.mad.model.Chapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * Dialog Fragment containing filter form.
 */
public class SocMediaDialogFragment extends DialogFragment implements View.OnClickListener {


    private View rootView;

    private Button yes;
    private Button cancel;

    private EditText facebookID;
    private EditText instagramID;
    private EditText twitterID;

    private DocumentReference chapterRef;
    private Map<String, String>socialMedia;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_filter, container, false);

        yes = (Button)rootView.findViewById(R.id.button_set_media_yes);
        cancel = (Button)rootView.findViewById(R.id.button_cancel);
        facebookID = (EditText) rootView.findViewById(R.id.editText_facebook);
        instagramID = (EditText) rootView.findViewById(R.id.editText_instagram);
        twitterID = (EditText) rootView.findViewById(R.id.editText_twitter);


        yes.setOnClickListener(this);
        cancel.setOnClickListener(this);

        chapterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                socialMedia = (Map<String, String>) documentSnapshot.get("socialMedia");
                if(socialMedia.containsKey("facebook")){
                    facebookID.setText(socialMedia.get("facebook"));
                }
                if(socialMedia.containsKey("instagram")){
                    instagramID.setText(socialMedia.get("instagram"));
                }
                if(socialMedia.containsKey("twitter")){
                    twitterID.setText(socialMedia.get("twitter"));        }


            }
        });


        return rootView;
    }

    public void setChapterRef(DocumentReference chapterRef){
        this.chapterRef = chapterRef;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_set_media_yes:
                setMedia();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void setMedia() {
        Map<String, String> newMedia = new HashMap<String, String>();

        if(facebookID.getText().toString()!=null && !facebookID.getText().toString().equals("")){
            newMedia.put("facebook", facebookID.getText().toString());
        }
        if(instagramID.getText().toString()!=null && !instagramID.getText().toString().equals("")){
            newMedia.put("instagram", instagramID.getText().toString());
        }
        if(twitterID.getText().toString()!=null && !twitterID.getText().toString().equals("")){
            newMedia.put("twitter", twitterID.getText().toString());
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("socialMedia", newMedia);

        chapterRef.update(updates);


        dismiss();
        Toast.makeText(getContext(), "Social Media Changed", Toast.LENGTH_SHORT).show();

    }

    public void onCancelClicked() {

        dismiss();
    }


}
