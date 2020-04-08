package com.hg.mad.model;

public class Officer {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_POSITION = "position";
    public static final String FIELD_CONTACT = "contact";
    public static final String FIELD_PROFILEIMAGE = "profileImage";

    private String name;
    private String position;
    private String contact;
    private byte[] profileImage;

    public Officer() {}

    public Officer(String name, String position, String contact){
        this.name = name;
        this.position = position;
        this.contact = contact;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
