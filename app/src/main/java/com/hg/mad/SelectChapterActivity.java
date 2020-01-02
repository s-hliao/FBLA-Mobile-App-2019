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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.Model.Chapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class SelectChapterActivity extends AppCompatActivity{

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

        join = findViewById(R.id.button_join);
        create = findViewById(R.id.button_create);
        selectChapter = findViewById(R.id.select_chapter);
        chapterName = findViewById(R.id.text_chapter_name);
        selectChapterBtn = findViewById(R.id.button_select_chapter);

        chapterName.setVisibility(View.GONE);
        selectChapter.setVisibility(View.GONE);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chapterName.setVisibility(View.GONE);
                selectChapter.setVisibility(View.VISIBLE);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chapterName.setVisibility(View.VISIBLE);
                selectChapter.setVisibility(View.GONE);
            }
        });
    }
}