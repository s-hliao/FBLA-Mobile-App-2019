package com.hg.mad.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.HashSet;

public class ChapterEvent {
    public static final String FIELD_EVENTNAME = "eventName";
    public static final String FIELD_EVENTTYPE = "eventType";
    public static final String FIELD_DESCRIPTION = "Description";
    public static final String FIELD_SIGNEDUP = "signedUp";
    public static final String FIELD_CHAPTERNAME = "chapterName";

    private String eventName;
    private String eventType;
    private String description;
    private HashMap<String, Integer> signedUp;
    private final String chapterName;

    public ChapterEvent(String eventName, String eventType, String description, HashMap<String, Integer> signedUp, String chapterName){
        this.eventName = eventName;
        this.eventType = eventType;
        this.description = description;
        this.signedUp = signedUp;
        this.chapterName = chapterName;

    }

    public String getEventName(){
        return eventName;
    }
    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    public String getEventType(){
        return eventType;
    }

    public void setEventType(String eventType){
        this.eventType = eventType;
    }

    public String getDescription(){return description;}

    public void setDescription(String description){
        this.description = description;
    }

    public HashSet<String> getSignedUp(){
        return (HashSet<String>)signedUp.keySet();
    }

    public String getChapterName() {
        return chapterName;
    }

    public void addUser(String user){
        signedUp.put(user, signedUp.size());
    }

    public void resetUsers(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference users = firestore.collection("DatabaseUser");

        users.whereEqualTo("inChapter", chapterName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // If the list is empty, add the new user
                            // SIGN UP STAGE
                            if (task.getResult()!= null && task.getResult().size() > 0){
                                for(QueryDocumentSnapshot result: task.getResult()) {
                                    DatabaseUser user = result.toObject(DatabaseUser.class);
                                    user
                                    
                                }
                            }
                        }
                    }
                });
        }



        signedUp = new HashMap<>();
    }
}
