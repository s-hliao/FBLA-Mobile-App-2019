package com.hg.mad.Model;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.HashSet;

@IgnoreExtraProperties
public class DatabaseUser {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_INCHAPTER = "inChapter";
    public static final String FIELD_ISADMIN = "isAdmin";
    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_EVENTSSIGNEDDUP = "eventsSignedUp";

    private String name;
    private Boolean inChapter;
    private Boolean isAdmin;
    private String chapterName;
    private HashMap<String,Integer> eventsSignedUp;

    public DatabaseUser() {}

    public DatabaseUser(String name, boolean inChapter, boolean isAdmin,
                        String chapterName, HashMap<String, Integer> eventsSignedUp) {
        this.name = name;
        this.inChapter = inChapter;
        this.isAdmin = isAdmin;
        this.chapterName = chapterName;
        this.eventsSignedUp = eventsSignedUp;
    }

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

    public Boolean getIsAdmin(){
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin){
        this.isAdmin = isAdmin;
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

    public void removeEvent(String eventName){

        eventsSignedUp.remove(eventName);
    }

    public void addEvent(String event){
        eventsSignedUp.put(event, eventsSignedUp.size());
    }

    public void setEventsSignedUp(HashMap<String, Integer> eventsSignedUp){
        this.eventsSignedUp = eventsSignedUp;
    }
}
