package com.hg.mad.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Officer {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_POSITION = "position";
    public static final String FIELD_CONTACT = "contact";
    public static final String FIELD_PROFILEIMAGE = "profileImage";

    private String name;
    private String position;
    private String contact;
    private byte[] profileImage;

    public Officer(String name, String position, String contact){
        this.name = name;
        this.position = position;
        this.contact = contact;
        profileImage = new byte[0];

    }

    public Officer(String name, String position, String contact, byte[]profileImage){
        this.name = name;
        this.position = position;
        this.contact = contact;
        this.profileImage = profileImage;

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

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }


    public static void createOfficerList(Map<Officer, String>chapOfficers){
        List<Officer>officers = new ArrayList<>();
        officers.addAll(chapOfficers.keySet());
    }
}
