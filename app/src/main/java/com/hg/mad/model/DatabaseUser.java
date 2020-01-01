package com.hg.mad.model;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class DatabaseUser {

    public static final String FIELD_USERID = "userID";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_INCHAPTER = "inChapter";
    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_EVENTSSIGNEDDUP = "eventsSignedUp";

    private String userID;
    private String name;
    private Boolean inChapter;
    private String chapterName;
    private HashMap<String,Integer> eventsSignedUp;

    public DatabaseUser() {}

    public DatabaseUser(String userID, String name, boolean inChapter,
                        String chapterName, HashMap<String, Integer> eventsSignedUp) {
        this.userID = userID;
        this.name = name;
        this.inChapter = inChapter;
        this.chapterName = chapterName;
        this.eventsSignedUp = eventsSignedUp;
    }

    public String getUserID(){return userID;}

    public void setUserID(String userID) {this.userID = userID;}

    public String getName() {
        return name;
    }

    public void setLastName(String name) {
        this.name = name;
    }

    public Boolean getInChapter(){
        return inChapter;
    }

    public void setInChapter(Boolean inChapter){
        this.inChapter = inChapter;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public HashMap<String, Integer> getEventsSignedUp(){
        return eventsSignedUp;
    }

    public void setEventsSignedUp(HashMap<String, Integer> eventsSignedUp){
        this.eventsSignedUp = eventsSignedUp;
    }
}
