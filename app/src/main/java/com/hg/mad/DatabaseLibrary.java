package com.hg.mad;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.Model.Chapter;
import com.hg.mad.Model.DatabaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseLibrary extends AppCompatActivity {

    // Current user
    FirebaseUser user;
    // Collection of users
    CollectionReference users;
    // FireStore
    FirebaseFirestore fireStore;

    // FIREBASE AUTHORIZATION
    void authInstance() {

        // Get authorization instance
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Current user
        user = auth.getCurrentUser();

        // a list of providers enabled for sign in
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        // Display sign in options
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.logo)
                        .build(), 100
        );

    }

    // When sign in flow is complete
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // if sign in succeeded, redirect the user
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
        }
    }

    // FIRESTORE MANAGEMENT

    void fireStoreManagement(){

        // Get FireStore instance
        fireStore = FirebaseFirestore.getInstance();

        // Get a collection of users named DatabaseUser
        users = fireStore.collection("DatabaseUser");

        // Adding a new user to DatabaseUser
        users.add(new DatabaseUser(
                user.getUid(),
                user.getDisplayName(),
                false,
                false,
                "",
                new HashMap<String, Integer>()
        ));

        // Get a list of users in DatabaseUser where the field "userId" is equal to the user's id
        users.whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            // Get the first user as a Database object & can access its fields normally
                            DatabaseUser databaseUser = task.getResult().getDocuments().get(0).toObject(DatabaseUser.class);

                            // You can also iterate through each document
                            for (QueryDocumentSnapshot document : task.getResult()) {

                            }

                            // To update fields, use DocumentReference
                            DocumentReference databaseUserRef = task.getResult().getDocuments().get(0).getReference();
                            databaseUserRef.update("field", "new value");
                        }
                    }
                });
    }
}
