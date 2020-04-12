package com.hg.mad.ui;

import android.annotation.SuppressLint;
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



    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapter, container, false);

        officerRV = (RecyclerView) getView().findViewById(R.id.recyclerView);
        facebook  = (ImageView) getView().findViewById(R.id.imageView7);
        instagram = (ImageView) getView().findViewById(R.id.imageView8);
        twitter = (ImageView) getView().findViewById(R.id.imageView9);
        addOfficerButton = (ImageView) getView().findViewById(R.id.imageView);
        mediaButton = (Button) getView().findViewById(R.id.media_button);

        facebook.setOnClickListener(this);
        instagram.setOnClickListener(this);
        twitter.setOnClickListener(this);
        addOfficerButton.setOnClickListener(this);
        mediaButton.setOnClickListener(this);



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
        } else{
            addOfficerButton.setVisibility(View.INVISIBLE);
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

    }

    @Override
    public void onOfficerSelected(DocumentSnapshot chapOfficer) {
        if(curUser.getIsAdmin()){

        }
    }
}
