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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hg.mad.Model.DatabaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AuthUiActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseUser user;
    private Context context;
    private CollectionReference usersCollection;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        // If the user is signed in, automatically redirect the user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();
            redirectUser();
        }

        // If a user is not signed in, show the auth ui activity on click
        setContentView(R.layout.auth_ui_activity);
        Button btn_sign_in = findViewById(R.id.button_sign_in);
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
    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN
        );
    }

    // When sign in flow is complete
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            // If sign in succeeded, redirect the user
            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                redirectUser();
            }
        }
    }

    // Adding the user to the database
    private void redirectUser() {

        // Initializing database
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        usersCollection = fireStore.collection("DatabaseUser");
        DocumentReference databaseUserRef = usersCollection.document(user.getUid());

        databaseUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    // If the user exists
                    if (document.exists()) {

                        DatabaseUser databaseUser = document.toObject(DatabaseUser.class);
                        // If the user isn't in a chapter, redirect to select chapter
                        if (!databaseUser.getInChapter()) {
                            startActivity(SelectChapterActivity.createIntent(context));
                            finish();
                        }

                        // Otherwise go to signed in activity
                        else {
                            startActivity(SignedInActivity.createIntent(context, null));
                            finish();
                        }
                    }

                    // If the user doesn't exist
                    else {

                        // add the user to the database
                        usersCollection.document(user.getUid()).set(new DatabaseUser(
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
                }
            }
        });
    }
}
