package com.hg.mad.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChapterEvent {
    public static final String FIELD_EVENTNAME = "eventName";
    public static final String FIELD_EVENTTYPE = "eventType";
    public static final String FIELD_DESCRIPTION = "Description";
    public static final String FIELD_SIGNEDUP = "signedUp";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_SIGNINKEY = "signInKey";
    public static final String FIELD_SIGNEDIN = "signedIn";
    public static final String FIELD_ATTENDANCEACTIVE = "attendanceActive";

    private String eventName;
    private String eventType;
    private String description;
    private Map<String, String> signedUp;
    private Date date;
    private String chapterName;
    private String signInKey;
    private Map<String, String> signedIn;
    private boolean attendanceActive;

    public ChapterEvent() {}

    public ChapterEvent(String eventName, String eventType, String description, HashMap<String, String> signedUp, Date date, String chapterName,
                        String signInKey, HashMap<String, String>signedIn, boolean attendanceActive){
        this.eventName = eventName;
        this.eventType = eventType;
        this.description = description;
        this.signedUp = signedUp;
        this.chapterName = chapterName;
        this.date = date;
        this.signInKey = signInKey;
        this.signedIn = signedIn;
        this.attendanceActive = attendanceActive;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addSignedUp(String userID, String userName){
        signedUp.put(userID, userName);
    }

    public void removeSignedUp(String userID){
        signedUp.remove(userID);
    }

    public void clearSignedUp(){
        signedUp.clear();
    }

    public String getSignInKey() {
        return signInKey;
    }

    public void setSignInKey(String signInKey){
        this.signInKey = signInKey;
    }

    public Map<String, String> getSignedIn() {
        return signedIn;
    }

    public void addSignedIn(String userID, String userName){
        signedIn.put(userID, userName);
    }

    public void removeSignedIn(String userID){
        signedIn.remove(userID);
    }

    public void clearSignedIn(){
        signedIn.clear();
    }

    public boolean getAttendanceActive() {
        return attendanceActive;
    }

    public void setAttendanceActive(boolean attendanceActive) {
        this.attendanceActive = attendanceActive;
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
                            if (task.getResult()!= null && task.getResult().size() > 0){
                                for(QueryDocumentSnapshot result: task.getResult()) {
                                    DatabaseUser user = result.toObject(DatabaseUser.class);
                                    if(user.getEventsSignedUp().containsKey(eventName)) {
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
