package com.hg.mad.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Attendee {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_POSITION = "signedIn";

    private String name;
    private boolean isSignedIn;

    public Attendee(){}

    public Attendee(String name, boolean signedIn){
        this.name = name;
        this.isSignedIn = signedIn;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean getSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.isSignedIn = signedIn;
    }
}
