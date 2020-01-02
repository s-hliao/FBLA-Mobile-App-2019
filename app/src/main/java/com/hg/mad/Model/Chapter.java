package com.hg.mad.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.HashSet;

public class Chapter {

    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_USERSINCHAPTER = "usersInChapter";
    public static final String FIELD_adminID = "adminID";

    private String chapterName;
    private HashMap<String, Integer> usersInChapter;
    private String adminID;
    private CollectionReference competitiveEvents;
    private CollectionReference chapterEvents;

    public Chapter() {}

    public Chapter(String chapterName, String adminID, HashMap<String, Integer>usersInChapter) {
        this.chapterName = chapterName;
        this.adminID = adminID;

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

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String newAdminID) {
        adminID = newAdminID;
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