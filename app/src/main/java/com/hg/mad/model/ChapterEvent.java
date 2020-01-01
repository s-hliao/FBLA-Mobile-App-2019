package com.hg.mad.model;

import java.util.HashMap;
import java.util.HashSet;

public class ChapterEvent {
    public static final String FIELD_EVENTNAME = "eventName";
    public static final String FIELD_EVENTTYPE = "eventType";
    public static final String FIELD_DESCRIPTION = "Description";
    public static final String FIELD_SIGNEDUP = "signedUp";

    private String eventName;
    private String eventType;
    private String description;
    private HashMap<String, Integer> signedUp;

    public ChapterEvent(String eventName, String eventType, String description, HashMap<String, Integer> signedUp){
        this.eventName = eventName;
        this.eventType = eventType;
        this.description = description;
        this.signedUp = signedUp;

    }

    public String getEventName(){
        return eventName;
    }
    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    public HashSet<String> getSignedUp(){
        return (HashSet<String>)signedUp.keySet();
    }

    public void addUser(String user){
        signedUp.put(user, signedUp.size());
    }

    public void resetUsers(){

    }
}
