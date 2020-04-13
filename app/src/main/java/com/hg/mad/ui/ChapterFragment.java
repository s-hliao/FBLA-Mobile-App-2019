package com.hg.mad.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.R;
import com.hg.mad.SignedInActivity;
import com.hg.mad.adapter.CompetitiveAdapter;
import com.hg.mad.adapter.OfficerAdapter;
import com.hg.mad.dialog.AddOfficerDialogFragment;
import com.hg.mad.dialog.EditOfficerDialogFragment;
import com.hg.mad.dialog.SocMediaDialogFragment;
import com.hg.mad.model.Chapter;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.model.Officer;
import com.hg.mad.util.ThisUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Signed;

public class ChapterFragment extends Fragment implements
        View.OnClickListener, OfficerAdapter.OnOfficerListener{

    private ImageView facebook;
    private ImageView instagram;
    private ImageView twitter;
    private LinearLayout addOfficerButton;
    private TextView mediaButton;


    private RecyclerView officerRV;

    private DocumentReference chapterRef;
    private FirebaseFirestore firestore;
    private Query query;

    private OfficerAdapter adapter;

    private SocMediaDialogFragment socMediaDialog;
    private AddOfficerDialogFragment addOfficerDialog;
    private EditOfficerDialogFragment editOfficerDialog;


    private boolean isAdmin;
    private String chapterName;

    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapter, container, false);

        officerRV = (RecyclerView) root.findViewById(R.id.recyclerView);
        facebook  = (ImageView) root.findViewById(R.id.imageView7);
        instagram = (ImageView) root.findViewById(R.id.imageView8);
        twitter = (ImageView) root.findViewById(R.id.imageView9);
        addOfficerButton = (LinearLayout) root.findViewById(R.id.button_addOfficer);
        mediaButton = (TextView) root.findViewById(R.id.media_button);

        facebook.setOnClickListener(this);
        instagram.setOnClickListener(this);
        twitter.setOnClickListener(this);
        addOfficerButton.setOnClickListener(this);
        mediaButton.setOnClickListener(this);

        socMediaDialog = new SocMediaDialogFragment();
        addOfficerDialog = new AddOfficerDialogFragment();
        editOfficerDialog = new EditOfficerDialogFragment();

        isAdmin = false;

        FirebaseFirestore.setLoggingEnabled(true);

        firestore = FirebaseFirestore.getInstance();

        // Only show manage to admins

        isAdmin = ThisUser.isAdmin();
        if (isAdmin){
            addOfficerButton.setVisibility(View.VISIBLE);
            mediaButton.setVisibility(View.VISIBLE);
        } else{
            addOfficerButton.setVisibility(View.INVISIBLE);
            mediaButton.setVisibility(View.INVISIBLE);
        }

        chapterName = ThisUser.getChapterName();

        final OfficerAdapter.OnOfficerListener o = this;

        Task<QuerySnapshot> q = firestore.collection("Chapter").orderBy("chapterName")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds: queryDocumentSnapshots.getDocuments()){
                            if(ds.get("chapterName").toString().equals(chapterName)){
                                chapterRef = ds.getReference();
                                query = chapterRef.collection("officers").orderBy("name");
                                adapter = new OfficerAdapter(query, o);
                                adapter.setQuery(query);
                                officerRV.setLayoutManager(new LinearLayoutManager(getContext()));

                                officerRV.setAdapter(adapter);
                                officerRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


                            }
                        }

                    }
                });


        return root;
    }



    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


    @Override
    public void onClick(View view) {
        switch( view.getId()){
            case R.id.media_button: // set social media
                socMediaDialog.setChapterRef(chapterRef);
                getFragmentManager().executePendingTransactions();
                if(!socMediaDialog.isAdded()){
                    socMediaDialog.show(getFragmentManager(), "socMediaDialog");
                }
                break;
            case R.id.button_addOfficer: // add a new officer
                addOfficerDialog.setChapterRef(chapterRef);
                getFragmentManager().executePendingTransactions();
                if(!addOfficerDialog.isAdded())
                    addOfficerDialog.show(getFragmentManager(), "addOfficerDialog");
                break;
            case R.id.imageView7: // facebook
                onSocialMediaClicked("facebook");
                break;
            case R.id.imageView8: //instagram
                onSocialMediaClicked("instagram");
                break;
            case R.id.imageView9: //twitter
                onSocialMediaClicked("twitter");
                break;
        }

    }


    public void onSocialMediaClicked(final String s){
        chapterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, String>socialMedia = (Map<String, String>) documentSnapshot.get("socialMedia");
                if(socialMedia.containsKey(s)){
                    System.out.println("http://www."+s+".com/"+socialMedia.get(s));
                    Intent viewIntent = new Intent("android.intent.action.VIEW",
                            Uri.parse("http://www."+s+".com/"+socialMedia.get(s)));
                    startActivity(viewIntent);
                } else{
                    Toast.makeText(getContext(), "Not set up", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onOfficerSelected(DocumentSnapshot chapOfficer) {
        System.out.println("selected");
        System.out.println(isAdmin);
        if(isAdmin){
            System.out.println("admin");
            editOfficerDialog.setOfficer(chapOfficer);
            getFragmentManager().executePendingTransactions();
            if(!editOfficerDialog.isAdded())
                editOfficerDialog.show(getFragmentManager(), "editOfficerDialog");
        }
    }
}
