package com.hg.mad.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Chapter {

    public static final String FIELD_CHAPTERNAME = "chapterName";
    public static final String FIELD_USERSINCHAPTER = "usersInChapter";
    public static final String FIELD_ADMINID = "adminID";
    public static final String FIELD_COMPETITIVEEVENTS = "competitiveEvents";
    public static final String FIELD_CHAPTEREVENTS = "chapterEvents";

    private String chapterName;
    private Map<String, Integer> usersInChapter;
    private String adminID;
    private CollectionReference competitiveEvents;
    private CollectionReference chapterEvents;

    public Chapter() {}

    public Chapter(String chapterName, String adminID, Map<String, Integer>usersInChapter) {
        this.chapterName = chapterName;
        this.adminID = adminID;
        this.usersInChapter = usersInChapter;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void addUser(String user) {
        usersInChapter.put(user, usersInChapter.size());
    }

    public Set<String> getUsers() {
        return (Set<String>) usersInChapter.keySet();
    }

    public void resetUsers() {
        usersInChapter = new HashMap<>();
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String newAdminID) {
        adminID = newAdminID;
    }

    public void addCompetitiveEvent(final CompetitiveEvent compEvent) {
        competitiveEvents.whereEqualTo("eventName", compEvent.getEventName()) //search for event with same name
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!(task.isSuccessful() && (task.getResult() != null))) {
                                //if an event with that name doesn't exist yet
                            competitiveEvents.add(compEvent);
                        }
                    }
                });
    }

    public void removeCompetitiveEvent(String eventName){ // remove competitive event by name
        competitiveEvents.whereEqualTo("eventName",eventName) //search for event with same name
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            for(DocumentSnapshot result: task.getResult().getDocuments()) { //get results from query
                               result.getReference().delete();
                            }
                        }
                    }
                });
    }

    public void addChapterEvent(final ChapterEvent chapEvent) {
        chapterEvents.whereEqualTo("eventName", chapEvent.getEventName()) //search for event with same name
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!(task.isSuccessful() && (task.getResult() != null))) {
                            //if an event with that name doesn't exist yet
                            competitiveEvents.add(chapEvent);
                        }
                    }
                });
    }

    public void removeChapterEvent(String eventName){
        chapterEvents.whereEqualTo("eventName",eventName) //search for event with same name
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            for(DocumentSnapshot result: task.getResult().getDocuments()) { //get results from query
                                result.getReference().delete();
                            }
                        }
                    }
                });
    }

    public void resetEvents(){
        competitiveEvents.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && (task.getResult() != null)) {
                    for(DocumentSnapshot result: task.getResult().getDocuments()) { //get results from query
                        result.getReference().delete();
                    }
                }
            }
        });;

        chapterEvents.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && (task.getResult() != null)) {
                    for(DocumentSnapshot result: task.getResult().getDocuments()) { //get results from query
                        result.getReference().delete();
                    }
                }
            }
        });
    }


}
