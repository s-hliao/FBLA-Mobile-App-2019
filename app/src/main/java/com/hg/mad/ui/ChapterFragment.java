package com.hg.mad.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.hg.mad.adapter.CompetitiveAdapter;
import com.hg.mad.adapter.OfficerAdapter;
import com.hg.mad.dialog.SocMediaDialogFragment;
import com.hg.mad.model.Chapter;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.model.Officer;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment implements
        View.OnClickListener, OfficerAdapter.OnOfficerListener{

    private ImageView facebook;
    private ImageView instagram;
    private ImageView twitter;
    private ImageView addOfficerButton;
    private Button mediaButton;

    private RecyclerView officerRV;

    private DocumentReference chapterRef;
    private FirebaseFirestore firestore;
    private Query query;

    private DatabaseUser curUser;
    private OfficerAdapter adapter;

    private SocMediaDialogFragment socMediaDialog;




    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapter, container, false);

        officerRV = (RecyclerView) root.findViewById(R.id.recyclerView);
        facebook  = (ImageView) root.findViewById(R.id.imageView7);
        instagram = (ImageView) root.findViewById(R.id.imageView8);
        twitter = (ImageView) root.findViewById(R.id.imageView9);
        addOfficerButton = (ImageView) root.findViewById(R.id.imageView);
        mediaButton = (Button) root.findViewById(R.id.media_button);

        facebook.setOnClickListener(this);
        instagram.setOnClickListener(this);
        twitter.setOnClickListener(this);
        addOfficerButton.setOnClickListener(this);
        mediaButton.setOnClickListener(this);

        socMediaDialog = new SocMediaDialogFragment();




        FirebaseFirestore.setLoggingEnabled(true);

        firestore = FirebaseFirestore.getInstance();



        // Only show manage to admins
        DocumentReference databaseUserRef = firestore.collection("DatabaseUser").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    curUser = task.getResult().toObject(DatabaseUser.class);

                }
            }
        });


        if (curUser.getIsAdmin()){
            addOfficerButton.setVisibility(View.VISIBLE);
            mediaButton.setVisibility(View.VISIBLE);
        } else{
            addOfficerButton.setVisibility(View.INVISIBLE);
            mediaButton.setVisibility(View.INVISIBLE);
        }

        Task<QuerySnapshot> q = firestore.collection("Chapters").whereEqualTo("chapterName", curUser.getChapterName())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        chapterRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        chapterRef.collection("officers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                adapter = new OfficerAdapter(chapterRef.collection("officers"));

                                officerRV.setLayoutManager(new LinearLayoutManager(getContext()));
                                officerRV.setAdapter(adapter);
                                officerRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                            }
                        });
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
                if(!socMediaDialog.isAdded()){
                    socMediaDialog.show(getFragmentManager(), "socMediaDialog");
                }
                break;
            case R.id.imageView: // add a new officer
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
                Chapter chap =  documentSnapshot.toObject(Chapter.class);
                if(chap.getSocialMedia().containsKey(s)){
                    Intent viewIntent = new Intent("android.intent.action.VIEW",
                            Uri.parse("http://www."+s+".com/"+chap.getSocialMedia().get(s)));
                }

            }
        });
    }

    @Override
    public void onOfficerSelected(DocumentSnapshot chapOfficer) {
        if(curUser.getIsAdmin()){

        }
    }
}
