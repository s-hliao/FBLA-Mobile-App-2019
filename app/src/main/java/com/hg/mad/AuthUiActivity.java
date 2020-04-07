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

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    // When the user is NOT signed in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_ui_activity);

        // Enable Terms of Use Hyperlinks
        TextView textView = findViewById(R.id.text_TOC);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        // A list of providers enabled for sign in
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // When the continue button is clicked, show sign in options
        Button btn_sign_in = findViewById(R.id.button_sign_in);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInOptions();
            }
        });
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
                StaticMethods.redirect(this);
            }
        }
    }
}
