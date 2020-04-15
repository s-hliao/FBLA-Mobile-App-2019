package com.hg.mad.model;

import java.util.HashMap;
import java.util.Map;

public class Chapter {

    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_ADMINID = "adminID";
    public static final String FIELD_COMPETITIVEEVENTS = "competitiveEvents";
    public static final String FIELD_CHAPTEREVENTS = "chapterEvents";
    public static final String FIELD_SOCIALMEDIA = "socialMedia";

    private String chapterName;
    private String adminID;
    private Map<String, Map<String, String>> competitiveEvents;
    private Map<String, Map<String, String>> chapterEvents;
    private Map<String, String> socialMedia;


    public Chapter() {}

    public Chapter(String chapterName, String adminID) {
        this.chapterName = chapterName;
        this.adminID = adminID;
        this.competitiveEvents = new HashMap<>();
        this.chapterEvents = new HashMap<>();
        this.socialMedia = new HashMap<>();
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public Map<String, Map<String, String>> getCompetitiveEvents(){
        return competitiveEvents;
    }

    public void setCompetitiveEvents(Map<String, Map<String, String>> competitiveEvents){
        this.competitiveEvents = competitiveEvents;
    }

    public Map<String, Map<String, String>> getChapterEvents(){
        return chapterEvents;
    }

    public void setChapterEvents(Map<String, Map<String, String>> chapterEvents){
        this.chapterEvents = chapterEvents;
    }

    public Map<String, String> getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(Map<String, String> socMedia) {
        this.socialMedia = socMedia;
    }
}
