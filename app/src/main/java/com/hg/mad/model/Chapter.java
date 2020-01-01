package com.hg.mad.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private CollectionReference competitiveEventReference;
    private CollectionReference chapterEventReference;


    public Chapter(String chapterName, String admin, HashMap<String, Integer>usersInChapter) {
        this.chapterName = chapterName;
        this.admin = admin;

        this.usersInChapter = usersInChapter;
       /* CollectionReference chapters = FirebaseFirestore.getInstance().collection("Chapter");

        chapters.whereEqualTo("chapterName", chapterName).get()
                @Override
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){

                }*/
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

    public void addChapterEvent() {

    }

    public void resetEvents(){

    }



}
