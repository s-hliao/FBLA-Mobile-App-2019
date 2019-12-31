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
    FirebaseFirestore firestore;
    CollectionReference users;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // If the user is signed in, show the signed in activity
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            user = auth.getCurrentUser();
            addUserToDatabase();
            startActivity(SignedInActivity.createIntent(this, null));
            finish();
        }
        // If the user is not signed in, show the authuiactivity
        setContentView(R.layout.auth_ui_activity);
        btn_sign_in = findViewById(R.id.button_sign_in);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInOptions();
            }
        });

        // Enable Terms of Use Hyperlinks
        TextView textView =findViewById(R.id.text_TOC);
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
                .setLogo(R.drawable.logo)
                .build(),RC_SIGN_IN
        );
    }

    // When sign in flow is complete
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // If sign in succeeded, go to signed in activity
            if (resultCode == RESULT_OK){
                user = FirebaseAuth.getInstance().getCurrentUser();
                addUserToDatabase();
                startActivity(SignedInActivity.createIntent(this, response));
            }
        }
    }

    // Adding the user to the database if they aren't in it
    private void addUserToDatabase(){

        // Initializing database
        firestore = FirebaseFirestore.getInstance();
        users = firestore.collection("DatabaseUser");

        // Get a list of users
        users.whereEqualTo("userID", user.getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        // If the list is empty, add the new user
                        if (task.getResult()!= null && task.getResult().size() == 0){
                            users.add(new DatabaseUser(
                                    user.getUid(),
                                    user.getDisplayName(),
                                    false,
                                    false,
                                    "default"
                            ));
                        }
                    }
                }
            });
    }
}
