package com.hg.mad.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class Chapter {

    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_USERSINCHAPTER = "usersInChapter";
    public static final String FIELD_adminID = "adminID";

    private final String chapterName;
    private HashMap<String, Integer> usersInChapter;
    private String admin;
    private CollectionReference competitiveEvents;
    private CollectionReference chapterEvents;


    public Chapter(String chapterName, String admin, HashMap<String, Integer>usersInChapter) {
        this.chapterName = chapterName;
        this.admin = admin;

        this.usersInChapter = usersInChapter;
       CollectionReference chapters = FirebaseFirestore.getInstance().collection("Chapter");
        final String[] chapterID = new String[1];
        chapters.whereEqualTo("chapterName", chapterName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null  && task.getResult().size()>0) {
                            if (task.getResult()!= null && task.getResult().size() > 0){
                                chapterID[0] = task.getResult().getDocuments().get(0).getId();
                            }

                        }
                    }


                });

        competitiveEvents = FirebaseFirestore.getInstance().collection("Chapter").document(chapterID[0]).collection("competitiveEvents");
        chapterEvents = FirebaseFirestore.getInstance().collection("Chapter").document(chapterID[0]).collection("chapterEvents");
    }

    public String getChapterName() {
        return chapterName;
    }

    public void addUser(String user) {
        usersInChapter.put(user, usersInChapter.size());
    }

    public HashSet<String> getUsers() {
        return (HashSet<String>) usersInChapter.keySet();
    }

    public void resetUsers() {
        usersInChapter = new HashMap<>();
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String newAdmin) {
        admin = newAdmin;
    }

    public void addCompetitiveEvent() {

    }

    public void removeCompetitiveEvent(){

    }

    public void addChapterEvent() {

    }

    public void removeChapterEvent(){

    }

    public void resetEvents(){

    }



}
