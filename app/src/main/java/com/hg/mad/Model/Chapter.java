package com.hg.mad.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Chapter {

    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_ADMINID = "adminID";
    public static final String FIELD_USERSINCHAPTER = "usersInChapter";
    public static final String FIELD_COMPETITIVEEVENTS = "competitiveEvents";
    public static final String FIELD_CHAPTEREVENTS = "chapterEvents";

    private String chapterName;
    private Map<String, String> usersInChapter;
    private String adminID;
    private Map<String, CompetitiveEvent> competitiveEvents;
    private Map<String, ChapterEvent> chapterEvents;

    public Chapter() {}

    public Chapter(String chapterName, String adminID, Map<String, String>usersInChapter) {
        this.chapterName = chapterName;
        this.adminID = adminID;
        this.usersInChapter = usersInChapter;
        competitiveEvents = new HashMap<>();
        chapterEvents = new HashMap<>();
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String newAdminID) {
        adminID = newAdminID;
    }

    public Map<String, String> getUsersInChapter() {return usersInChapter;}

    public void setUsersInChapter(Map<String, String> usersInChapter) {this.usersInChapter = usersInChapter;}

    public void addUser(String Uid, String user) {usersInChapter.put(Uid, user);}

    public void resetUsers() {
        usersInChapter = new HashMap<>();
    }

    public void addCompetitiveEvent(CompetitiveEvent compEvent) {
        if(!competitiveEvents.containsKey(compEvent.getEventName()))
            competitiveEvents.put(compEvent.getEventName(), compEvent);
    }

    public void removeCompetitiveEvent(String eventName){ // remove competitive event by name
        competitiveEvents.remove(eventName);
    }

    public void addChapterEvent(ChapterEvent chapEvent) {
        if(!chapterEvents.containsKey(chapEvent.getEventName()))
            chapterEvents.put(chapEvent.getEventName(), chapEvent);
    }

    public void removeChapterEvent(String eventName){
        chapterEvents.remove(eventName);
    }

    public void resetEvents(){
        competitiveEvents.clear();
        chapterEvents.clear();
    }


}
