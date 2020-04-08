package com.hg.mad.model;

public class CompetitiveEvent {

    public static final String FIELD_EVENTNAME = "eventName";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_INTRO = "intro";

    private String eventName;
    private String type;
    private String category;
    private Boolean intro;

    public CompetitiveEvent() {}

    public CompetitiveEvent(String eventName, String type, String category, Boolean intro){
        this.eventName = eventName;
        this.type = type;
        this.category = category;
        this.intro = intro;
    }

    public String getEventName(){
        return eventName;
    }

    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIntro() {
        return intro;
    }

    public void setIntro(Boolean intro) {
        this.intro = intro;
    }
}