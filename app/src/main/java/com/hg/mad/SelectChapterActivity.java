package com.hg.mad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
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
import com.hg.mad.Model.Chapter;
import com.hg.mad.Model.DatabaseUser;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectChapterActivity extends AppCompatActivity{

    private Context context;

    FirebaseUser user;
    CollectionReference usersCollection;
    DatabaseUser databaseUser;
    DocumentReference databaseUserRef;

    SearchableSpinner chapterSpinner;
    FirebaseFirestore fireStore;
    ArrayList<String> chapterNames;

    RadioButton join;
    RadioButton create;
    LinearLayout selectChapter;
    TextView chapterName;
    Button selectChapterBtn;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SelectChapterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_chapter_activity);

        context = getApplicationContext();

        // Set up the chapter spinner
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

        // Set up views
        join = findViewById(R.id.button_join);
        create = findViewById(R.id.button_create);
        selectChapter = findViewById(R.id.select_chapter);
        chapterName = findViewById(R.id.text_chapter_name);
        selectChapterBtn = findViewById(R.id.button_select_chapter);

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

        // Get the current user & database user reference
        user = FirebaseAuth.getInstance().getCurrentUser();
        usersCollection = fireStore.collection("DatabaseUser");
        databaseUserRef = usersCollection.document(user.getUid());

        // When the continue button is clicked
        selectChapterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if the user is joining an existing chapter
                if (join.isChecked()) {
                    if (chapterSpinner.getSelectedItem() == null) {
                        Toast.makeText(getApplicationContext(), "Please select a chapter", Toast.LENGTH_LONG).show();
                    } else {
                        databaseUserRef.update("chapterName", chapterSpinner.getSelectedItem().toString());
                        databaseUserRef.update("inChapter", true);
                        startActivity(SignedInActivity.createIntent(context, null));
                        finish();
                    }
                }

                // if the user is creating a new chapter
                else if (create.isChecked()) {
                    if (chapterName.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Please enter a chapter name", Toast.LENGTH_LONG).show();
                    }

                    // TODO handle duplicate chapters
                    //else if (){

                    //}

                    else {
                        // Get a collection of chapters
                        CollectionReference chaptersCollection = fireStore.collection("Chapter");

                        // Adding a new chapter
                        Map<String, Object> data = new HashMap<>();
                        data.put("chapterName", chapterName.getText().toString());
                        data.put("adminID", user.getUid());

                        Map<String, String> usersInChapter = new HashMap<String, String>();
                        usersInChapter.put(user.getUid(), user.getDisplayName());
                        data.put("usersInChapter", usersInChapter);

                        chaptersCollection.add(data);

                        // Updating the user's information
                        databaseUserRef.update("chapterName", chapterName.getText().toString());
                        databaseUserRef.update("inChapter", true);
                        databaseUserRef.update("isAdmin", true);
                        startActivity(SignedInActivity.createIntent(context, null));
                        finish();
                    }
                }

                // if the user hasn't selected anything
                else {
                    Toast.makeText(getApplicationContext(), "Please select or join a chapter", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}