package com.hg.mad;

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
import com.hg.mad.Model.DatabaseUser;

import java.util.HashMap;

public class StaticMethods {

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

                            DatabaseUser databaseUser = document.toObject(DatabaseUser.class);

                            // If the user isn't in a chapter, redirect to select chapter
                            if (!databaseUser.getInChapter()) {
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
                                    "",
                                    new HashMap<String, Integer>()
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
