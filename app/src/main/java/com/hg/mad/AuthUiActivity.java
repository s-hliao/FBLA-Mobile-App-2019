package com.hg.mad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hg.mad.model.DatabaseUser;

import java.util.Arrays;
import java.util.List;

public class AuthUiActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    List<AuthUI.IdpConfig> providers;
    FirebaseAuth auth;
    Button btn_sign_in;
    FirebaseUser user;
    FirebaseFirestore fireStore;
    CollectionReference users;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If the user is signed in, automatically redirect the user
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            user = auth.getCurrentUser();
            redirectUser();
        }

        // If the user is not signed in, show the auth ui activity
        setContentView(R.layout.auth_ui_activity);
        btn_sign_in = findViewById(R.id.button_sign_in);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInOptions();
            }
        });
        // Enable Terms of Use Hyperlinks
        TextView textView = findViewById(R.id.text_TOC);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // A list of providers enabled for sign in
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
    }

    // Displays sign in options
    private void showSignInOptions(){
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .build(),RC_SIGN_IN
                //.setLogo(R.drawable.logo)
        );
    }

    // When sign in flow is complete
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // If sign in succeeded, redirect the user
            if (resultCode == RESULT_OK){
                user = FirebaseAuth.getInstance().getCurrentUser();
                redirectUser();
            }
        }
    }

    // Adding the user to the database
    private void redirectUser(){

        // Initializing database
        fireStore = FirebaseFirestore.getInstance();
        users = fireStore.collection("DatabaseUser");

        // Get a list of users
        users.whereEqualTo("userID", user.getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult()!= null) {

                        // If it's a new user (not in a chapter yet)
                        if (task.getResult().isEmpty()){
                            users.add(new DatabaseUser(
                                    user.getUid(),
                                    user.getDisplayName(),
                                    false,
                                    "",
                                    null
                            ));
                            // redirect to select chapter activity
                            redirectSelectChapter();
                        }

                        // If the user already exists
                        else {
                            DatabaseUser databaseUser = task.getResult().getDocuments().get(0).toObject(DatabaseUser.class);
                            if (!databaseUser.getInChapter()) {
                                redirectSelectChapter();
                            } else {
                                redirectSignedIn();
                            }
                        }
                    }
                }
            });
    }

    private void redirectSelectChapter(){
        startActivity(SelectChapterActivity.createIntent(this));
        finish();
    }

    private void redirectSignedIn(){
        startActivity(SignedInActivity.createIntent(this, null));
        finish();
    }
}
