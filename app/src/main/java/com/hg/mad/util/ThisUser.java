package com.hg.mad.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hg.mad.AuthUiActivity;
import com.hg.mad.SelectChapterActivity;
import com.hg.mad.SignedInActivity;
import com.hg.mad.model.DatabaseUser;

import javax.annotation.Signed;


public class ThisUser{

    private FirebaseFirestore fireStore;
    private static DocumentSnapshot userSnapshot;
    private static DocumentReference userRef;

    private static String uid;
    private static String displayName;
    private static Boolean isAdmin;
    private static String chapterName;

    // First time initializing
    public ThisUser(final SignedInActivity activity) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        displayName = auth.getCurrentUser().getDisplayName();
        fireStore = FirebaseFirestore.getInstance();

        final CollectionReference usersCollection = fireStore.collection("DatabaseUser");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = usersCollection.document(user.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userSnapshot = task.getResult();
                    chapterName = (String) userSnapshot.get("chapterName");
                    isAdmin = (Boolean) userSnapshot.get("isAdmin");
                    activity.execute();
                }
            }
        });
    }

    public static DocumentSnapshot getUserSnapshot() {
        return userSnapshot;
    }

    public static DocumentReference getUserRef() {
        return userRef;
    }

    public static String getUid() {
        return uid;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static Boolean isAdmin() {
        return isAdmin;
    }

    public static String getChapterName() {
        return chapterName;
    }

    public static void redirect(final AppCompatActivity thisActivity) {

        // If the user is signed in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
            final CollectionReference usersCollection = fireStore.collection("DatabaseUser");

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference databaseUserRef = usersCollection.document(user.getUid());

            databaseUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        // If the user exists in firebase
                        if (document.exists()) {

                            // If the user isn't in a chapter, redirect to select chapter
                            if (! (Boolean) document.get("inChapter")) {
                                thisActivity.startActivity(SelectChapterActivity.createIntent(thisActivity.getApplicationContext()));
                                thisActivity.finish();
                            }

                            // Otherwise go to signed in activity
                            else {
                                thisActivity.startActivity(SignedInActivity.createIntent(thisActivity.getApplicationContext(), null));
                                thisActivity.finish();
                            }
                        }

                        // If the user doesn't exist
                        else {

                            // Add the user to the database
                            usersCollection.document(user.getUid()).set(new DatabaseUser(
                                    user.getDisplayName(),
                                    false,
                                    false,
                                    ""
                            ));

                            // Redirect to select chapter activity
                            thisActivity.startActivity(SelectChapterActivity.createIntent(thisActivity.getApplicationContext()));
                            thisActivity.finish();
                        }
                    }
                }
            });
        }

        // If a user is not signed in, show the auth ui activity
        else {
            thisActivity.startActivity(AuthUiActivity.createIntent(thisActivity.getApplicationContext()));
            thisActivity.finish();
        }
    }
}
