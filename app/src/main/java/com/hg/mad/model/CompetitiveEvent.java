package com.hg.mad.model;

import java.util.HashMap;
import java.util.HashSet;

public class CompetitiveEvent {

    public static final String FIELD_EVENTNAME = "eventName";
    public static final String FIELD_STATECAPPED = "isStateCapped";
    public static final String FIELD_STATECAP = "stateCap";
    public static final String FIELD_USERS = "signedUp";

    private String eventName;
    private boolean isStateCapped;
    private int stateCap;
    private HashMap<String, Integer> signedUp;

    public CompetitiveEvent(String eventName, boolean isStateCapped, int stateCap, HashMap<String, Integer> signedUp){
        this.eventName = eventName;
        this.isStateCapped = isStateCapped;
        this.stateCap = stateCap;
        this.signedUp = signedUp;

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

    public void reset(){

    }

}
