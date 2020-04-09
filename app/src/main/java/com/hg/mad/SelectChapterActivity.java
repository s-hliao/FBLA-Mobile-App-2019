package com.hg.mad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.model.Chapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectChapterActivity extends AppCompatActivity{

    private Context context;

    FirebaseUser user;
    CollectionReference usersCollection;
    DocumentReference databaseUserRef;
    CollectionReference chapterCollection;

    SearchableSpinner chapterSpinner;
    FirebaseFirestore fireStore;
    ArrayList<String> chapterNames;

    RadioButton join;
    RadioButton create;
    LinearLayout selectChapter;
    TextView chapterName;
    Button selectChapterBtn;

    Toast myToast;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SelectChapterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_chapter_activity);

        context = getApplicationContext();

        // Set up views
        join = findViewById(R.id.button_join);
        create = findViewById(R.id.button_create);
        selectChapter = findViewById(R.id.select_chapter);
        chapterName = findViewById(R.id.text_chapter_name);
        selectChapterBtn = findViewById(R.id.button_select_chapter);

        initializeSpinner();
        initializeToggle();

        obtainUserAndChapter();

        // When the continue button is clicked
        selectChapterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the user is joining an existing chapter
                if (join.isChecked()) {

                    if (chapterSpinner.getSelectedItem() == null) {
                        alert("Please select a chapter");
                    }

                    // Update the user and chapter, then go to signed in activity
                    else {
                        String spinnerText = chapterSpinner.getSelectedItem().toString();
                        updateUser(spinnerText, true, false);
                        updateChapter(spinnerText, user.getUid(), user.getDisplayName());

                        startActivity(SignedInActivity.createIntent(context, null));
                        finish();
                    }
                }

                // If the user is creating a new chapter
                else if (create.isChecked()) {
                    final String cName = chapterName.getText().toString();

                    if (cName.equals("")){
                        alert("Please enter a chapter name");
                    } else {

                        // Handle duplicate chapters
                        chapterCollection.whereEqualTo("chapterName", cName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) {
                                        alert("This chapter name is already taken");
                                    } else {
                                        // Create the chapter, add the user as admin, then go to signed in activity
                                        createChapter(cName, user.getUid(), user.getDisplayName());
                                        updateUser(cName, true, true);

                                        startActivity(SignedInActivity.createIntent(context, null));
                                        finish();
                                    }
                                }
                            }
                        });
                    }
                }

                // if the user hasn't selected anything
                else {
                    alert("Please select or join a chapter");
                }
            }
        });
    }

    // Set up the chapter spinner
    private void initializeSpinner(){
        chapterSpinner = findViewById(R.id.spinner_select_chapter);
        fireStore = FirebaseFirestore.getInstance();
        chapterNames = new ArrayList<>();

        fireStore.collection("Chapter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        chapterNames.add(document.toObject(Chapter.class).getChapterName());
                    }
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chapterNames);
        chapterSpinner.setAdapter(adapter);
    }

    // Set up the toggle
    private void initializeToggle(){

        // Start with both selections turned off
        chapterName.setVisibility(View.GONE);
        selectChapter.setVisibility(View.GONE);

        // Toggle join chapter selections
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chapterName.setVisibility(View.GONE);
                selectChapter.setVisibility(View.VISIBLE);
            }
        });

        // Toggle select chapter selections
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chapterName.setVisibility(View.VISIBLE);
                selectChapter.setVisibility(View.GONE);
            }
        });
    }

    // Get the current user & database user reference
    private void obtainUserAndChapter(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        usersCollection = fireStore.collection("DatabaseUser");

        databaseUserRef = usersCollection.document(user.getUid());
        chapterCollection = fireStore.collection("Chapter");
    }

    private void updateUser(String chapterName, Boolean inChapter, Boolean isAdmin){
        databaseUserRef.update("chapterName", chapterName);
        databaseUserRef.update("inChapter", inChapter);
        databaseUserRef.update("isAdmin", isAdmin);
    }

    // Creating a new chapter using chapter name and admin info
    private void createChapter(String name, String adminID, String adminName){

        Map<String, String> usersInChapter = new HashMap<>();
        usersInChapter.put(adminID, adminName);
        Chapter chapter = new Chapter(name, adminID, usersInChapter);

        fireStore.collection("Chapter").add(chapter);
    }

    // Updating a chapter with a new user
    private void updateChapter(String chapterName, final String userID, final String userName){
        chapterCollection.whereEqualTo("chapterName", chapterName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot chapterSnapshot = task.getResult().getDocuments().get(0);
                    Chapter chapterJoined = chapterSnapshot.toObject(Chapter.class);
                    chapterJoined.addUser(userID, userName);

                    DocumentReference chapterRef = chapterCollection.document(chapterSnapshot.getId());
                    chapterRef.update("usersInChapter", chapterJoined.getUsersInChapter());
                }
            }
        });
    }

    private void alert(String message) {
        if(myToast != null)
            myToast.cancel();
        myToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        myToast.show();
    }
}