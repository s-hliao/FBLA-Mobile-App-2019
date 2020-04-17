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
    public static final String FIELD_DATE = "date";
    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_SIGNINKEY = "signInKey";
    public static final String FIELD_ATTENDANCEACTIVE = "attendanceActive";

    private String eventName;
    private String eventType;
    private String description;
    private Date date;
    private String chapterName;
    private String signInKey;
    private boolean attendanceActive;

    public ChapterEvent() {}

    public ChapterEvent(String eventName, String eventType, String description,Date date, String chapterName,
                        String signInKey, boolean attendanceActive){
        this.eventName = eventName;
        this.eventType = eventType;
        this.description = description;
        this.chapterName = chapterName;
        this.date = date;
        this.signInKey = signInKey;
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

    public String getChapterName() {
        return chapterName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSignInKey() {
        return signInKey;
    }

    public void setSignInKey(String signInKey){
        this.signInKey = signInKey;
    }

    public boolean getAttendanceActive() {
        return attendanceActive;
    }

    public void setAttendanceActive(boolean attendanceActive) {
        this.attendanceActive = attendanceActive;
    }
}
