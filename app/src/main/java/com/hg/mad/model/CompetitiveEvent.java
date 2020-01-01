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

public class CompetitiveEvent {

    public static final String FIELD_EVENTNAME = "eventName";
    public static final String FIELD_STATECAPPED = "isStateCapped";
    public static final String FIELD_STATECAP = "stateCap";
    public static final String FIELD_USERS = "signedUp";
    public static final String FIELD_CHAPTERNAME = "chapterName";

    private String eventName;
    private boolean isStateCapped;
    private int stateCap;
    private HashMap<String, Integer> signedUp;
    private final String chapterName;

    public CompetitiveEvent(String eventName, boolean isStateCapped, int stateCap, HashMap<String, Integer> signedUp, String chapterName){
        this.eventName = eventName;
        this.isStateCapped = isStateCapped;
        this.stateCap = stateCap;
        this.signedUp = signedUp;
        this.chapterName = chapterName;

    }
    public String getEventName(){
        return eventName;
    }
    public void setEventName(String eventName){
        this.eventName = eventName;
    }
    public boolean getStateCapped(){
        return isStateCapped;
    }
    public void setStateCapped(boolean isStateCapped){
        this.isStateCapped = isStateCapped;
    }

    public String getChapterName() {
        return chapterName;
    }
    public int getStateCap(){
        return stateCap;
    }
    public void setStateCap(int stateCap){
        this.stateCap = stateCap;
    }

    public HashSet<String> getSignedUp(){
        return (HashSet<String>)signedUp.keySet();
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
                                    if(user.getEventsSignedUp().contains(eventName)) {
                                        user.removeEvent(eventName);
                                    }

                                }
                            }
                        }
                    }
                });
        signedUp = new HashMap<>();
    }

}
