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
import com.hg.mad.Model.DatabaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AuthUiActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth auth;
    private Button btn_sign_in;
    private FirebaseUser user;
    private FirebaseFirestore fireStore;
    private CollectionReference users;
    private Context context;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        // if the user is signed in, automatically redirect the user
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();
            redirectUser();
        }

        // if the user is not signed in, show the auth ui activity
        setContentView(R.layout.auth_ui_activity);
        btn_sign_in = findViewById(R.id.button_sign_in);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInOptions();
            }
        });
        // enable Terms of Use Hyperlinks
        TextView textView = findViewById(R.id.text_TOC);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // a list of providers enabled for sign in
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
    }

    // displays sign in options
    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN
                //.setLogo(R.drawable.logo)
        );
    }

    // when sign in flow is complete
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // if sign in succeeded, redirect the user
            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                redirectUser();
            }
        }
    }

    // adding the user to the database
    private void redirectUser() {

        // initializing database
        fireStore = FirebaseFirestore.getInstance();
        users = fireStore.collection("DatabaseUser");

        // get a list of users
        users.whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            // if it's a new user (not in a chapter yet)
                            if (task.getResult().isEmpty()) {
                                // add the user to the database
                                users.add(new DatabaseUser(
                                        user.getUid(),
                                        user.getDisplayName(),
                                        false,
                                        false,
                                        "",
                                        new HashMap<String, Integer>()
                                ));
                                // redirect to select chapter activity
                                startActivity(SelectChapterActivity.createIntent(context));
                                finish();
                            }

                            // if the user already exists
                            else {
                                DatabaseUser databaseUser = task.getResult().getDocuments().get(0).toObject(DatabaseUser.class);

                                // if the user isn't in a chapter, redirect to select chapter
                                if (!databaseUser.getInChapter()) {
                                    startActivity(SelectChapterActivity.createIntent(context));
                                    finish();
                                }
                                // otherwise go to signed in activity
                                else {
                                    startActivity(SignedInActivity.createIntent(context, null));
                                    finish();
                                }
                            }
                        }
                    }
                });
    }
}
