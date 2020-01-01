package com.hg.mad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SelectChapterActivity extends AppCompatActivity {

    Button btn_sign_out;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SelectChapterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_chapter_activity);

        // Sign out when the sign out button is clicked
        btn_sign_out = findViewById(R.id.button_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(SelectChapterActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            // Show sign in screen
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(AuthUiActivity.createIntent(SelectChapterActivity.this));
                                    finish();
                                } else {
                                    // TODO
                                }
                            }
                        });
            }
        });
    }


}