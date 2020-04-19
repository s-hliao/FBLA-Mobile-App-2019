package com.hg.mad.model;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class DatabaseUser {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_INCHAPTER = "inChapter";
    public static final String FIELD_ISADMIN = "isAdmin";
    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_COMPETITIVEEVENTS = "competitiveEvents";

    private String name;
    private Boolean inChapter;
    private Boolean isAdmin;
    private String chapterName;
    private HashMap<String, Integer> competitiveEvents;

    public DatabaseUser() {}

    public DatabaseUser(String name, boolean inChapter, boolean isAdmin, String chapterName) {
        this.name = name;
        this.inChapter = inChapter;
        this.isAdmin = isAdmin;
        this.chapterName = chapterName;
        this.competitiveEvents = new HashMap<>();
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

    public HashMap<String, Integer> getCompetitiveEvents(){
        return competitiveEvents;
    }

    public void setCompetitiveEvents(HashMap<String, Integer> competitiveEvents){
        this.competitiveEvents = competitiveEvents;
    }

}
