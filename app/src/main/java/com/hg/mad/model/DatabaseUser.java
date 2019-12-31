package com.hg.mad.model;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DatabaseUser {

    public static final String FIELD_USERID = "userID";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ISADMIN = "isAdmin";
    public static final String FIELD_INCHAPTER = "inChapter";
    public static final String FIELD_CHAPTERNAME = "chapterName";

    private String userID;
    private String name;
    private Boolean isAdmin;
    private Boolean inChapter;
    private String chapterName;

    public DatabaseUser() {}

    public DatabaseUser(String userID, String name, boolean isAdmin, boolean inChapter,
                        String chapterName) {
        this.userID = userID;
        this.name = name;
        this.isAdmin = isAdmin;
        this.inChapter = inChapter;
        this.chapterName = chapterName;
    }

    public String getUserID(){return userID;}

    public void setUserID(String userID) {this.userID = userID;}

    public String getName() {
        return name;
    }

    public void setLastName(String name) {
        this.name = name;
    }

    public Boolean getIsAdmin(){
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    public Boolean getInChapter(){
        return inChapter;
    }

    public void setInChapter(Boolean inChapter){
        this.inChapter = inChapter;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}
